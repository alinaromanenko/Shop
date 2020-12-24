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
import java.util.ArrayList;

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

    @RequestMapping(value = "shop/{id}", method = RequestMethod.GET)
    public ModelAndView list(@PathVariable("id") Long id) {
        Person person = this.shopRepository.findPersonById(id);
        if(person.getIsSeller().equals("false")){
            Iterable<Item> items = this.shopRepository.findAll();
            ModelAndView mav = new ModelAndView();
            mav.setViewName("items/list");
            mav.addObject("items", items);
            mav.addObject("person", person);
            return mav;
        }
        else {
            Iterable<Item> items = this.shopRepository.findSellerItems(person);
            ModelAndView mav = new ModelAndView();
            mav.setViewName("items/list");
            mav.addObject("items", items);
            mav.addObject("person", person);
            return mav;
        }

    }

    @RequestMapping(value = "delete/{id}")
    public ModelAndView delete(@PathVariable("id") Long id) {
        Item item = this.shopRepository.findItem(id);
        Long sellerId = item.getSellerId();
        this.shopRepository.delete(item);
        return new ModelAndView("redirect:/shop/{item.sellerId}", "item.sellerId", sellerId);
    }



    @RequestMapping(value ="shop/{sid}/{id}")
    public ModelAndView view(@PathVariable("id") Item item, @PathVariable("sid") Long id) {
        Person person = this.shopRepository.findPersonById(id);
        String name = this.shopRepository.findPersonById(item.getSellerId()).getFirstName();
        ModelAndView mav = new ModelAndView();
        mav.addObject("item", item);
        mav.addObject("person", person);
        mav.addObject("sellerName", name);
        if (person.getIsSeller().equals("true")){
            mav.setViewName("items/edit");
        }
        else {
            mav.setViewName("items/view");
        }
        return mav;
    }

    @RequestMapping(value = "create/{id}", method = RequestMethod.GET)
    public ModelAndView createForm(@ModelAttribute Item item, @PathVariable("id") Long id) {
        Person person = this.shopRepository.findPersonById(id);
        return new ModelAndView("items/form", "person", person);
    }

    @RequestMapping(value = "login", method = RequestMethod.GET)
    public String loginEnter(@ModelAttribute Person person) {
        return "items/login";
    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public ModelAndView create(@Valid Person person, BindingResult result,
                               RedirectAttributes redirect) throws IOException {
        if (person.getPhone() != null) {
            if (this.shopRepository.findPerson(person) != null) {
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
            Person fullPerson = this.shopRepository.findPerson(person);
            if ( fullPerson != null) {
                return new ModelAndView("redirect:/shop/{fullPerson.id}", "fullPerson.id", fullPerson.getId());
            }
            else {
                redirect.addFlashAttribute("globalMessage", "Неверный email или пароль.");
                return new ModelAndView("redirect:/login");
            }
        }
    }


    @RequestMapping(value = "form", method = RequestMethod.POST)
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
        return new ModelAndView("redirect:/shop/{item.sellerId}", "item.sellerId", item.getSellerId());
    }

    @RequestMapping(value = "edit/{id}", method = RequestMethod.POST)
    public ModelAndView edit(@Valid Item item, BindingResult result,
                             RedirectAttributes redirect,  @RequestParam ("file") MultipartFile file) throws IOException {
        if (result.hasErrors()) {
            return new ModelAndView("items/edit", "formErrors", result.getAllErrors());
        }
        if (file.isEmpty()) {

            item.setImage(this.shopRepository.findItem(item.getId()).getImage());
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
        System.out.println(item.getName());
        System.out.println(item.getPrice());
        this.shopRepository.edit(item);

        return new ModelAndView("redirect:/shop/{item.sellerId}", "item.sellerId", item.getSellerId());
    }

    @RequestMapping(value = "history/{id}")
    public ModelAndView history(@ModelAttribute Item item, @PathVariable("id") Long id) {
        Person person = this.shopRepository.findPersonById(id);
        return new ModelAndView("items/history", "person", person);
    }

    @RequestMapping(value = "cart")
    public ModelAndView cart(@ModelAttribute("cart") ArrayList<Item> cart, @ModelAttribute("id") Long id) {
        Person person = this.shopRepository.findPersonById(id);
        Long total = 0L;
        for(Item item:cart){
            total+=item.getPrice();
        }
        ModelAndView mav = new ModelAndView();
        mav.addObject("total", total);
        mav.addObject("items", cart);
        mav.addObject("person", person);
        mav.setViewName("items/cart");
        return mav;
    }


    @RequestMapping(value = "/adding/{id}/{items}")
    public String adding(@PathVariable("id") Long id, @PathVariable("items") String items, final RedirectAttributes redirectAttributes) {
        ArrayList<Item> cart = new ArrayList<>();
        if (!items.equals("null")) {
            for (String ids : items.split("q")) {
                cart.add(this.shopRepository.findItem(Long.parseLong(ids)));
            }
        }
        redirectAttributes.addFlashAttribute("cart", cart);
        redirectAttributes.addFlashAttribute("id",id);
        return "redirect:/cart";
    }

    @RequestMapping("foo")
    public String foo() {
        throw new RuntimeException("Expected exception in controller");
    }


}