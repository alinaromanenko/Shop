package NSU.ui.mvc;

import javax.imageio.ImageIO;
import javax.validation.Valid;

import NSU.ui.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import NSU.ui.ShopRepository;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * @author Rob Winch
 */
@Controller
@RequestMapping("/")
public class ItemController {
	private final ShopRepository shopRepository;

	@Autowired
	public ItemController(ShopRepository shopRepository) {
		this.shopRepository = shopRepository;
	}

	@RequestMapping
	public ModelAndView nothing(){
		return new ModelAndView("layout");
	}

	@RequestMapping(params = "shop")
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

	private final String UPLOAD_DIR = "target/classes/static/images/";
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView create(@Valid Item item, @RequestParam("file") MultipartFile file, BindingResult result,
							   RedirectAttributes redirect) throws IOException {
		if (result.hasErrors()) {
			return new ModelAndView("items/form", "formErrors", result.getAllErrors());
		}
		item = this.shopRepository.save(item);
		System.out.println(item.getPrice());
		redirect.addFlashAttribute("globalMessage", "Товар успешно создан");

		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		System.out.println(fileName);
		System.out.println(file.isEmpty());

		try {
			Path path = Paths.get(UPLOAD_DIR + item.getName()+".jpg");
			item.setImage(item.getName()+".jpg");
			System.out.println(item.getImage());
			System.out.println(path);
			File convFile = new File(file.getOriginalFilename());
			convFile.createNewFile();
			FileOutputStream fos = new FileOutputStream(convFile);
			fos.write(file.getBytes());
			fos.close();

			BufferedImage image = ImageIO.read(convFile);
			File output = new File(String.valueOf(path));
			ImageIO.write(image, "jpg", output);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return new ModelAndView("redirect:/{item.id}", "item.id", item.getId());
	}
	@RequestMapping("foo")
	public String foo() {
		throw new RuntimeException("Expected exception in controller");
	}

}
