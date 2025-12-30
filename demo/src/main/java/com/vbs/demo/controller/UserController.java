package com.vbs.demo.controller;

import com.vbs.demo.dto.DisplayDto;
import com.vbs.demo.dto.LoginDto;
import com.vbs.demo.dto.UpdateDto;
import com.vbs.demo.models.History;
import com.vbs.demo.models.Transaction;
import com.vbs.demo.models.User;
import com.vbs.demo.repositories.HistoryRepo;
import com.vbs.demo.repositories.TransactionRepo;
import com.vbs.demo.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    UserRepo userRepo;

    @Autowired
    HistoryRepo historyRepo;

    @Autowired
    TransactionRepo transactionRepo;
    @PostMapping("/register")
    public  String register(@RequestBody User user)
    {
        History h1 = new History();
        h1.setDescription("Signup ");
        userRepo.save(user);
        historyRepo.save(h1);
        return "Signup Successfully";
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginDto u)
    {
        User user = userRepo.findByUsername(u.getUsername());
        if(user == null)
        {
            return "User not found";
        }
        if(!user.getPassword().equals(u.getPassword()))
        {
            return  "Password Incorrect";
        }
        if(!user.getRole().equals(u.getRole()))
        {
            return  "Role Incorrect";
        }

        return String.valueOf(user.getId());

    }

    @GetMapping("/get-details/{id}")
    public DisplayDto display(@PathVariable int id)
    {
        User user = userRepo.findById(id).orElseThrow(() -> new RuntimeException("user not found"));
        DisplayDto displayDto = new DisplayDto();

        displayDto.setUsername(user.getUsername());
        displayDto.setBalance(user.getBalance());

        return displayDto;
    }

    @PostMapping("/update")
    public String update (@RequestBody UpdateDto obj)
    {
        User user = userRepo.findById(obj.getId()).orElseThrow(()-> new RuntimeException("Not found"));

//        History h1 = new History();
        if(obj.getKey().equalsIgnoreCase("name"))
        {
            if(user.getName().equals(obj.getValue())) return "Cannot be same";
            user.setName(obj.getValue());
//            historyRepo.save(h1);
        }

        else if(obj.getKey().equalsIgnoreCase("password"))
        {
            if(user.getPassword().equals(obj.getValue())) return "Cannot be same";
            user.setPassword(obj.getValue());
        }

        else if(obj.getKey().equalsIgnoreCase("email"))
        {
            if(user.getEmail().equals(obj.getValue())) return "Cannot be same";

            User user2 = userRepo.findByEmail(obj.getValue());
            if(user2 != null) return "Email Already Exists";

            user.setEmail(obj.getValue());
        }

        else{
            return  "Invalid key";
        }

        userRepo.save(user);
        return "Update successfully";
    }

    @PostMapping("/add/{adminId}")
    public String add(@RequestBody User user,@PathVariable int adminId)
    {
        History h1 = new History();
        h1.setDescription("Admin "+adminId+" Created User "+user.getUsername());
        historyRepo.save(h1);
        userRepo.save(user);

        if(user.getBalance()>0)
        {
            Transaction t = new Transaction();
            t.setAmount(user.getBalance());
            t.setCurrBalance(user.getBalance());
            t.setDescription("Rs. "+user.getBalance()+" Deposit Successful");
            t.setUserId(user.getId());
            transactionRepo.save(t);
        }
        return "Added Successfully";
    }

    //@RequestParam is always optional means if it is given then also ok if not then also ok
    //http:localhost:8081/login.html?id=3 ,,,here id =3 is optional,,which is indicated by (?)
    //http:localhost:8081/login.html/id=3 ,,,here id =3 is not optional,,which is indicated by (/)
    //example: dominosPizza/large -> dominos pizza should be large, it is a compulsion
    //example: dominosPizza/large?cheese=true -> dominos pizza should be large, it is a compulsion , but cheese is optional(true/false)
    @GetMapping("/users")
    public List<User> getAllUsers(@RequestParam String sortBy, @RequestParam String order)
    {
        Sort sort;  //Sort(class) is default class provided by springboot
        if(order.equalsIgnoreCase("desc"))
        {
            sort = Sort.by(sortBy).descending();  //Sort.by() is a function in which we pass, condition(id,name,userName,etc) based on which we have to sort
        }
        else{
            sort = Sort.by(sortBy).ascending();
        }

        return userRepo.findAllByRole("customer",sort);
    }


    //keyword : main jo search bar main username type kar rahi hoon woh
    @GetMapping("/users/{keyword}")
    public List<User> getUser(@PathVariable String keyword)
    {
        return userRepo.findByUsernameContainingIgnoreCaseAndRole(keyword,"customer");
    }

    @DeleteMapping("delete-user/{userId}/admin/{adminId}")
    public String delete(@PathVariable int userId,@PathVariable int adminId)
    {
        User user = userRepo.findById(userId).orElseThrow(()->new RuntimeException("Not found"));

        if(user.getBalance()>0)
        {
            return "Balance Should be zero";
        }
        userRepo.delete(user);

        History h1 = new History();
        h1.setDescription("Admin "+adminId+" Deleted User "+user.getUsername());
        historyRepo.save(h1);

        return "Deleted successfully";
    }
}
