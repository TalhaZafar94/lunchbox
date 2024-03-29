package com.example.lunchbox.controller.foodmaker;

import com.example.lunchbox.model.entity.Foodmaker;
import com.example.lunchbox.model.entity.Order;
import com.example.lunchbox.model.entity.Ratings;
import com.example.lunchbox.service.FoodmakerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping(value = "/foodmaker")
public class FoodmakerLogincontroller {

    private FoodmakerService foodmakerService;
    @Value("${upload.path}")
    private String uploadPath;


    @Autowired
    public FoodmakerLogincontroller(FoodmakerService foodmakerService) {
        this.foodmakerService = foodmakerService;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Foodmaker verifyLogin(@RequestParam String userName, @RequestParam String password,
                                 HttpSession session, Model model, @RequestParam String token) {

        Foodmaker foodmaker = foodmakerService.login(userName, password, token);
        if (foodmaker == null) {
            model.addAttribute("loginError", "Error logging in. Please try again");
            return null;
        }
        session.setAttribute("loggedInUser", foodmaker);
        return foodmaker;
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpSession session) {
        session.removeAttribute("loggedInUser");
        return "login";
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public String signup(@RequestBody Foodmaker foodmaker) {
        //   if(foodmaker.getFoodmakerpassword() != null && foodmaker.getFoodmakerEmail() != null && foodmaker.getFoodmakerName() != null &&
        //           foodmaker.getFoodmakerNic() != null && foodmaker.getFoodmakerPhoneNumber() != null)
        //   {
        foodmakerService.foodmakerSignup(foodmaker);
        //foodmakerService.saveImage(image,foodmaker);
        return "foodmaker added";
        //   }
        // return "please specify the fields";
    }

    @RequestMapping(value = "/upload-img", method = RequestMethod.POST)
    public String uploadImage(@RequestParam Integer id, @RequestParam("file") MultipartFile file) {
        String uploadedPath = null;
        String final_Path = "http://localhost:8080/images/";
        foodmakerService.findAllFoodmakers();
        Foodmaker foodmaker = foodmakerService.getFoodmakerById(id);
        String UPLOADED_FOLDER = uploadPath;
        try {

            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
            Files.write(path, bytes);

            final_Path += file.getOriginalFilename();

            foodmaker.setFoodmakerImagePath(final_Path);
            uploadedPath = path.toString();
            foodmakerService.foodmakerSignup(foodmaker);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "{ \"uploadedPath\" : \"" + final_Path + "\"}";
    }


    @RequestMapping(value = "/update-password", method = RequestMethod.POST)
    public String updatePassword(@RequestParam String oldpassword, @RequestParam String newpassword, @RequestParam String foodmakerEmail) {
        if (foodmakerService.updatePassword(oldpassword, newpassword, foodmakerEmail)) {
            return "password updated";
        }
        return "error";
    }

    @RequestMapping(value = "/count-foodmakers", method = RequestMethod.GET)
    public long countFoodmakers() {
        return foodmakerService.countAllFoodMmkers();
    }

    @RequestMapping(value = "/foodmakers-nearBy-list", method = RequestMethod.GET)
    public List<Foodmaker> findNearByFoodmakers(@RequestParam Double lat, @RequestParam Double longt) {
        return foodmakerService.getFoodmakersNearBy(lat, longt);
    }


    @RequestMapping(value = "/foodmakers-list", method = RequestMethod.GET)
    public List<Foodmaker> findAllFoodmakers() {
        return foodmakerService.findAllFoodmakers();
    }

    @RequestMapping(value = "/delete-foodmaker", method = RequestMethod.POST)
    public String deleteFoodmaker(@RequestBody String foodmakerEmail) {
        foodmakerService.deleteFoodmaker(foodmakerEmail);
        return "foodmaker deleted";
    }

    @RequestMapping(value = "/search-foodmaker", method = RequestMethod.POST)
    public List<Foodmaker> searchFoodmakers(@RequestParam String foodmakerName) {
        return foodmakerService.getFoodmakerByname(foodmakerName);
    }

    @RequestMapping(value = "/foodmaker-listing", method = RequestMethod.GET)
    public ModelAndView adminDetail() {
        ModelAndView modelAndView = new ModelAndView("foodmaker-listing");
        return modelAndView;
    }

    @RequestMapping(value = "/add-foodmaker", method = RequestMethod.GET)
    public ModelAndView getAddAdminView() {
        ModelAndView modelAndView = new ModelAndView("add-foodmaker");
        return modelAndView;
    }

    @RequestMapping(value = "/add-foodmaker", method = RequestMethod.POST)
    public ModelAndView getOrderDetail(@RequestParam Integer rowId) {
        Foodmaker foodmaker = foodmakerService.getFoodmakerById(rowId);
        ModelAndView modelAndView = new ModelAndView("add-foodmaker");
        modelAndView.addObject("foodmakerDetail", foodmaker);

        return modelAndView;
    }

    @RequestMapping(value = "/set-status", method = RequestMethod.POST)
    public String setStatus(@RequestParam Integer foodmakerId, @RequestParam Integer status) {
        foodmakerService.setStatus(foodmakerId, status);
        return "{ \"status\" : \"" + status + "\"}";
    }

    @RequestMapping(value = "/set-ratings", method = RequestMethod.POST)
    public String setRatings(@RequestParam Integer customerId, @RequestParam Integer foodmakerId, @RequestParam Integer stars) {
        foodmakerService.setRatings(customerId, foodmakerId, stars);

        return "{\"status\":\"true\"}";
    }

    @RequestMapping(value = "/get-ratings", method = RequestMethod.GET)
    public List<Ratings> getRatings(@RequestParam Integer foodmakerId) {
        return foodmakerService.getRatingsByFoodmakerId(foodmakerId);
    }

    @RequestMapping(value = "/get-orderByFoodmakerId", method = RequestMethod.GET)
    public List<Order> getOrderByFoodmakerId(@RequestParam Integer foodmakerId) {
        return foodmakerService.getOrdersByfoodmakerId(foodmakerId);
    }

    @RequestMapping(value = "/get-ack-orderByFoodmakerId", method = RequestMethod.GET)
    public List<Order> getAckOrderByFoodmakerId(@RequestParam Integer foodmakerId) {
        return foodmakerService.getAckOrdersByfoodmakerId(foodmakerId);
    }


    @RequestMapping(value = "/get-done-orderByFoodmakerId", method = RequestMethod.GET)
    public List<Order> getDoneOrderByFoodmakerId(@RequestParam Integer foodmakerId) {
        return foodmakerService.getDoneOrdersByfoodmakerId(foodmakerId);
    }

}
