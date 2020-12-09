package NSU.ui.mvc;

import javax.imageio.ImageIO;
import javax.validation.Valid;

import NSU.ui.Item;
import NSU.ui.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import NSU.ui.ShopRepository;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Rob Winch
 */
@Controller
@RequestMapping("/")
public class ItemController {
    private final ShopRepository shopRepository;
    private final String UPLOAD_DIR = "target/classes/static/images/";


    @Autowired
    public ItemController(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
    }

    @RequestMapping
    public ModelAndView nothing() {
        return new ModelAndView("layout");
    }

    @RequestMapping(value = "shop")
    public ModelAndView list() {
        Iterable<Item> items = this.shopRepository.findAll();
        return new ModelAndView("items/list", "items", items);
    }

    @RequestMapping("{id}")
    public ModelAndView view(@PathVariable("id") Item item) {
        return new ModelAndView("items/view", "item", item);
    }

    @RequestMapping(params = "create", method = RequestMethod.GET)
    public String createForm(@ModelAttribute Item item) {
        return "items/form";
    }

    @RequestMapping(value = "login", method = RequestMethod.GET)
    public String loginEnter(@ModelAttribute Person person) {
        return "items/login";
    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public ModelAndView create(@Valid Person person, BindingResult result,
                               RedirectAttributes redirect) throws IOException {
        if (person.getPhone() != null) {
            if (this.shopRepository.findPerson(person) == null) {
                redirect.addFlashAttribute("globalMessage", "Вы уже зарегистрированы.");
                return new ModelAndView("redirect:/login");
            } else {
                Person registration = this.shopRepository.savePerson(person);
                if (registration == null) {
                    redirect.addFlashAttribute("globalMessage", "Пользователь с таким номером телефона уже зарегистрирован.");
                    return new ModelAndView("redirect:/login");
                } else {
                    redirect.addFlashAttribute("globalMessage", "Вы успешно зарегистрированы.");
                    return new ModelAndView("redirect:/login");
                }
            }
        } else {
            System.out.println(this.shopRepository.findPerson(person));
            //Для Даримы
            if (this.shopRepository.findPerson(person) == null) {
                return new ModelAndView("redirect:/shop");
            }
            else {
                redirect.addFlashAttribute("globalMessage", "Неверный email или пароль.");
                return new ModelAndView("redirect:/login");
            }
        }
    }


    @RequestMapping(params = "form", method = RequestMethod.POST)
    public ModelAndView create(@Valid Item item, BindingResult result, @RequestParam("file") MultipartFile file,
                               RedirectAttributes redirect) throws IOException {
        if (result.hasErrors()) {
            return new ModelAndView("items/form", "formErrors", result.getAllErrors());
        }
        if (file.isEmpty()) {
            item.setImage("no-image.png");
        } else {
            item.setImage(item.getName().hashCode() + ".jpg");
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            Path path = Paths.get(UPLOAD_DIR + item.getName().hashCode() + ".jpg");
            File convFile = new File(file.getOriginalFilename());
            convFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(convFile);
            fos.write(file.getBytes());
            fos.close();
            BufferedImage image = ImageIO.read(convFile);
            File output = new File(String.valueOf(path));
            ImageIO.write(image, "jpg", output);
        }
        item = this.shopRepository.save(item);
        redirect.addFlashAttribute("globalMessage", "Товар успешно создан");
        return new ModelAndView("redirect:/{item.id}", "item.id", item.getId());
    }


    @RequestMapping("foo")
    public String foo() {
        throw new RuntimeException("Expected exception in controller");
    }

}