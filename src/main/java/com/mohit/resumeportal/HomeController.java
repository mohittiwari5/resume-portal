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

        Optional<UserProfile> profileOptional = userProfileRepository.findByUserName("elon");
        profileOptional.orElseThrow(() -> new RuntimeException("Not found: "));

        UserProfile profile1 = profileOptional.get();

        Job job1 = new Job();
        job1.setCompany("Company 1");
        job1.setDesignation("Designation");
        job1.setId(1);
        job1.setStartDate(LocalDate.of(2020,1,1));
        job1.setCurrentJob(true);
        job1.getResponsibilities().add("Founder of SpaceX");
        job1.getResponsibilities().add("Founder of Tesla");
        job1.getResponsibilities().add("super minded person");

        Job job2 = new Job();
        job2.setCompany("Company 2");
        job2.setDesignation("Designation 2");
        job2.setId(2);
        job2.setStartDate(LocalDate.of(2019,5,1));
        job2.setEndDate(LocalDate.of(2020,1,1));
        job2.getResponsibilities().add("Founder of SpaceX");
        job2.getResponsibilities().add("Founder of Tesla");
        job2.getResponsibilities().add("super minded person");


        profile1.getJobs().clear();
        profile1.getJobs().add(job1);
        profile1.getJobs().add(job2);

        Education education1 = new Education();
        education1.setCollege("Subodh");
        education1.setQualification("BCA");
        education1.setSummary("Studied nothing");
        education1.setGPA("4.3");
        education1.setStartDate(LocalDate.of(2016,5,1));
        education1.setEndDate(LocalDate.of(2019,1,1));

        Education education2 = new Education();
        education2.setCollege("Compucom");
        education2.setQualification("MCA");
        education2.setSummary("Studied nothing");
        education2.setGPA("3.5");
        education2.setStartDate(LocalDate.of(2021,5,1));
        education2.setEndDate(LocalDate.of(2023,1,1));

        profile1.getEducations().clear();
        profile1.getEducations().add(education1);
        profile1.getEducations().add(education2);

        profile1.getSkills().clear();
        profile1.getSkills().add("Web Designing");
        profile1.getSkills().add("Graphics");
        profile1.getSkills().add("Gossip");


        userProfileRepository.save(profile1);

        return "profile";
    }

    @GetMapping("/edit")
    public String edit(Model model,Principal principal){

        String userName = principal.getName();
        //Here principal is used to get the logged in user
        model.addAttribute("userId",userName);
        Optional<UserProfile> userProfileOptional = userProfileRepository.findByUserName(userName);
        userProfileOptional.orElseThrow(() -> new RuntimeException("Not found: " + userName));
        UserProfile userProfile = userProfileOptional.get();
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




    @GetMapping("/view/{userId}")
    public String view(@PathVariable("userId") String userId, Model model){
        Optional<UserProfile> userProfileOptional = userProfileRepository.findByUserName(userId);

        userProfileOptional.orElseThrow(() -> new RuntimeException("Not found: " + userId));

        model.addAttribute("userId",userId);
        UserProfile userProfile = userProfileOptional.get();
        model.addAttribute("userProfile",userProfile);

        System.out.println("\n\n\n\n>>>>>>>>THEME IS::::>>>>>>>>>"+userProfile.getTheme()+"\n\n\n\n\n\n");

        return "profile-templates/" + userProfile.getTheme() + "/index";
    }
}
