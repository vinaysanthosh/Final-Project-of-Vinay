package controllers;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import DAO.productServices;
import model.Product;
import com.google.gson.Gson;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;


@EnableWebMvc
@Controller
public class HomeController {
	@Autowired
	  public productServices productService;
	

	  @Autowired
	  private JavaMailSender mailSender;
	  
	@Qualifier(value="productService")
  public void setPersonService(productServices ps){
      this.productService = ps;
  }
	

    //json productTableUsers
    @RequestMapping(value="/productTableUsers")
    public ModelAndView showproductTableUsers()
	{
		List<Product> listtojsp=new ArrayList<Product>();
		listtojsp=productService.listProduct();
		String json = new Gson().toJson(listtojsp);  // converting list into Google Gson object which is a string
		System.out.println(json);
		ModelAndView mv=new ModelAndView("productTableUsers");
		mv.addObject("myJson", json);
		return mv;
	}
    
    @RequestMapping("/recommend")
    public String showrecommend()
    {
    	return "recommend";
    } 
	
    @RequestMapping("/ProductDescription")
    public String showProductDescription()
    {
    	return "ProductDescription";
    }
  @RequestMapping("/")
  public String showIndex()
  {
  	return "index";
  }
  
  @RequestMapping("/index")
  public String showIndex1()
  {
  	return "index";
  }
  
  
  @RequestMapping("/signin")
  public String showSignIn()
  {
  	return "signin";
  }

@RequestMapping("/login")
  public String showLogin()
  {
	 
  	return "login";
  }

 
  @RequestMapping("/Register")
  public String showRegister()
  {
  	return "Register";
  }
    
  
  
  @RequestMapping(value="/Products",method=RequestMethod.GET )
  public String listProducts(Model model)
  {
      model.addAttribute("product", new Product());
      model.addAttribute("listProduct", this.productService.listProduct());
      return "/Products";
  }
  
  @RequestMapping("/details/{id}")
  public String getProductById(@PathVariable("id") int id,Model model) 
  {
  	model.addAttribute("product", this.productService.getProductById(id));    
      model.addAttribute("listProduct",this.productService.listProduct());
      return "Products";
  }
  
  @RequestMapping("delete/{id}")
  public String removeProduct(@PathVariable("id") int id)
  {
      this.productService.removeProduct(id);
      return "redirect:/Products";
  }
  
  @RequestMapping("/edit/{id}")
  public String editProduct(@PathVariable("id") int id, Model model)
  {
      model.addAttribute("product", this.productService.getProductById(id));
      model.addAttribute("listProduct", this.productService.listProduct());
      return "editpage";
  }
  
  @RequestMapping(value= "/AddProduct", method = RequestMethod.GET)
  public String addProduct(@ModelAttribute ("product") Product p)
  	
  {
  	if(p.getId()==0)
  	{
  		this.productService.addProduct(p);
  		
  	}
  	else
  	{
          
  	
  		this.productService.updateProduct(p);
  		
  	}
  	return "AddProduct";
      
  	}

  @RequestMapping(value= "/editpage/{id}", method = RequestMethod.GET)
  public String updateProduct(@PathVariable("id") int id,Model model,Product p)
  	
  {
  	this.productService.updateProduct(p);
  return "editpage";
  }

  @RequestMapping(value = "/uploadFile")
  String uploadFileHandler( )
  {
	  return "FileUploadSuccess";
  }
  
  private static final Logger logger=LoggerFactory.getLogger(HomeController.class);
  @RequestMapping(value = "/FileUploadSuccess", method = RequestMethod.POST)
  public @ResponseBody
  String uploadFileHandler( @RequestParam("file") MultipartFile file) 
  {
      if (!file.isEmpty()) 
      {
          try 
          {
              byte[] bytes = file.getBytes();
             System.out.println("after getbytes");
              // Creating the directory to store file
              String rootPath = "C:/temp";
              File dir = new File(rootPath + File.separator + file);
              if (!dir.exists())
                  dir.mkdirs();
              System.out.println("after dir");
              // Create the file on server
              File serverFile = new File(dir.getAbsolutePath() + File.separator + file.getOriginalFilename()+".png");
              BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
              stream.write(bytes);
              stream.close();

              logger.info("Server File Location="+ serverFile.getAbsolutePath());

              return "FileUploadSuccess" ;
            } 
          catch (Exception e) 
          {
              return "You failed to upload " ;
          }
      } 
      else 
      {
          return "You failed to upload  because the file was empty.";
      }
  }
  
  @RequestMapping(value = "/myForm", method = RequestMethod.GET)

  public String showForm(Map model) {

      return "/myForm";

  }
  

  @RequestMapping(value="/sendEmail", method = RequestMethod.POST)
  public String doSendEmail(HttpServletRequest request) {
      // takes input from e-mail form
      String recipientAddress = request.getParameter("email");
      String fname=request.getParameter("first_name");
      String subject ="musichub :: Your Friend Recommends..."+request.getParameter("musichub alert");
      String message = request.getParameter("comments");
      String finalmessage="Hi "+fname+", "+" "+message+"!!! "+"Check this out!!!";
       
      // prints debug info
      System.out.println("To: " + recipientAddress);
      System.out.println("Subject: " + subject);
      System.out.println("Message: " + finalmessage);
       
      // creates a simple e-mail object
      SimpleMailMessage email = new SimpleMailMessage();
      email.setTo(recipientAddress);
      email.setSubject(subject);
      email.setText(finalmessage);
       
      // sends the e-mail
      mailSender.send(email);
       
      // forwards to the view named "Result"
      return "redirect:/index";
  }
  
  


  @RequestMapping(value = "/login", method = RequestMethod.GET)
  public String login(ModelMap model) {
		 System.out.println("redirecting to login");

      return "login";
  }

  @RequestMapping(value = "/accessdenied", method = RequestMethod.GET)
  public String loginerror(ModelMap model) {
		 System.out.println("redirecting to login");

      model.addAttribute("error", "true");
      return "denied";
  }

  @RequestMapping(value = "/logout", method = RequestMethod.GET)
  public String logout(ModelMap model) {
      return "logout";
  }

  
		
}

