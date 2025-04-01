package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.mapper.InformationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class InformationController {

    @Autowired
    private InformationMapper  informationMapper;

    @GetMapping("/api/search/person/{id}")

    public List query(@PathVariable("id") String id){

        List<Person> list = informationMapper.getPersonById(id);
        System.out.println(list);
        return list;
    }

    @GetMapping("/api/search/user/{id}")

    public List query1(@PathVariable("id") String id){

        List<User> list = informationMapper.getUserById(id);
        System.out.println(list);
        return list;
    }

    @GetMapping("/api/search/counselor/{id}")

    public List query2(@PathVariable("id") String id){

        List<Counselor> list = informationMapper.getCounselorById(id);
        System.out.println(list);
        return list;
    }

    @GetMapping("/api/search/tutor/{id}")

    public List query3(@PathVariable("id") String id){

        List<Tutor> list = informationMapper.getTutorById(id);
        System.out.println(list);
        return list;
    }

    @PostMapping("/api/delete/person")
    public String delete(@RequestParam("id") String id){

        int i=informationMapper.deletePersonById(id);
        if(i > 0){
            return "success";
        }else{
            return "fail";
        }
    }

    @PostMapping("/api/delete/user")
    public String delete1(@RequestParam("id") String id){

        int i=informationMapper.deleteUserById(id);
        if(i > 0){
            return "success";
        }else{
            return "fail";
        }
    }

    @PostMapping("/api/delete/counselor")
    public String delete2(@RequestParam("id") String id){

        int i=informationMapper.deleteCounselorById(id);
        if(i > 0){
            return "success";
        }else{
            return "fail";
        }
    }

    @PostMapping("/api/delete/tutor")
    public String delete3(@RequestParam("id") String id){

        int i=informationMapper.deleteTutorById(id);
        if(i > 0){
            return "success";
        }else{
            return "fail";
        }
    }

    @PostMapping("/api/insert/person")
    public String insert(@RequestBody Person person){
        System.out.println("Received person data: " + person);
        int i=informationMapper.insertPerson(person);
        if(i > 0){
            return "success";
        }else{
            return "fail";
        }
    }

    @PostMapping("/api/insert/user")
    public String insert1(@RequestBody User user){
        System.out.println("Received user data: " + user);
        int i=informationMapper.insertUser(user);
        if(i > 0){
            return "success";
        }else{
            return "fail";
        }
    }

    @PostMapping("/api/insert/counselor")
    public String insert2(@RequestBody Counselor counselor){
        System.out.println("Received counselor data: " + counselor);
        int i=informationMapper.insertCounselor(counselor);
        if(i > 0){
            return "success";
        }else{
            return "fail";
        }
    }

    @PostMapping("/api/insert/tutor")
    public String insert3(@RequestBody Tutor tutor){
        System.out.println("Received tutor data: " + tutor);
        int i=informationMapper.insertTutor(tutor);
        if(i > 0){
            return "success";
        }else{
            return "fail";
        }
    }

    @PostMapping("/api/auth/change-password")
    public String changePassword(@RequestParam("id") String id, @RequestParam("password") String password){

        int i=informationMapper.changePassword(id, password);
        if(i > 0){
            return "success";
        }else{
            return "fail";
        }
    }

    @GetMapping("/api/person/last-login/{id}")

    public List lastLogin(@PathVariable("id") String id){

        List<Person> list = informationMapper.getLastLoginTime(id);
        System.out.println(list);
        return list;
    }

    @PostMapping("/api/user/avatar")
    public String changeAvatar(@RequestParam("idPictureLink") String idPictureLink, @RequestParam("id") String id){

        int i=informationMapper.changeAvatar(id, idPictureLink);
        if(i > 0){
            return "success";
        }else{
            return "fail";
        }
    }
}
