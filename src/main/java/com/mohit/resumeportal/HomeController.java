package com.mohit.resumeportal;

import com.mohit.resumeportal.models.Education;
import com.mohit.resumeportal.models.Job;
import com.mohit.resumeportal.models.User;
import com.mohit.resumeportal.models.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.DefaultStyledDocument;
import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {

    @Autowired
    UserProfileRepository userProfileRepository;

    @GetMapping("/")
    public String home(){
       return "index";
    }

    @GetMapping("/edit")
    public String edit(Model model,Principal principal,@RequestParam(required = false,defaultValue = "") String add){

        String userName = principal.getName();
        //Here principal is used to get the logged in user
        model.addAttribute("userId",userName);
        Optional<UserProfile> userProfileOptional = userProfileRepository.findByUserName(userName);
        userProfileOptional.orElseThrow(() -> new RuntimeException("Not found: " + userName));
        UserProfile userProfile = userProfileOptional.get();

        if(add.equals("job")){
            userProfile.getJobs().add(new Job());
        }
        if(add.equals("education")){
            userProfile.getEducations().add(new Education());
        }
        if(add.equals("skill")){
            userProfile.getSkills().add("");
        }

        model.addAttribute("userProfile",userProfile);
        return "edit";
    }

    @PostMapping("/edit")
    public String postEdit(Model model, Principal principal, @ModelAttribute UserProfile userProfile){

        String userName = principal.getName();
        Optional<UserProfile> userProfileOptional = userProfileRepository.findByUserName(userName);
        userProfileOptional.orElseThrow(() -> new RuntimeException("Not found: " + userName));
        UserProfile savedUserProfile = userProfileOptional.get();
        userProfile.setId(savedUserProfile.getId());
        userProfile.setUserName(savedUserProfile.getUserName());
        userProfile.setTheme(savedUserProfile.getTheme());
        userProfileRepository.save(userProfile);
        return "redirect:/view/"+userName;
    }

    @GetMapping("/delete")
    public String delete(Model model, Principal principal,@RequestParam String type,@RequestParam int index){

        String userName = principal.getName();
        Optional<UserProfile> userProfileOptional = userProfileRepository.findByUserName(userName);
        userProfileOptional.orElseThrow(() -> new RuntimeException("Not found: " + userName));
        UserProfile userProfile = userProfileOptional.get();

        if(type.equals("job")){
            userProfile.getJobs().remove(index);
        }
        if(type.equals("education")){
            userProfile.getEducations().remove(index);
        }
        if(type.equals("skill")){
            userProfile.getSkills().remove(index);
        }

        userProfileRepository.save(userProfile);
        return "redirect:/edit";

    }



    @GetMapping("/view/{userId}")
    public String view(Principal principal,@PathVariable("userId") String userId, Model model){

        if((principal != null) && !(principal.getName().equals(""))){
            boolean currentUserProfile = principal.getName().equals(userId);
            model.addAttribute("currentUserProfile",currentUserProfile);
        }

        String userName = principal.getName();
        Optional<UserProfile> userProfileOptional = userProfileRepository.findByUserName(userId);
        userProfileOptional.orElseThrow(() -> new RuntimeException("Not found: " + userId));
        model.addAttribute("userId",userId);
        UserProfile userProfile = userProfileOptional.get();
        model.addAttribute("userProfile",userProfile);

        System.out.println("\n\n\n\n>>>>>>>>THEME IS::::>>>>>>>>>"+userProfile.getTheme()+"\n\n\n\n\n\n");

        return "profile-templates/" + userProfile.getTheme() + "/index";
    }
}
