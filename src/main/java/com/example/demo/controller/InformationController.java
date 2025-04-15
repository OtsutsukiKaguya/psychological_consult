package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.entity.*;
import com.example.demo.mapper.InformationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class InformationController {

    private static final Logger logger = LoggerFactory.getLogger(InformationController.class);

    @Autowired
    private InformationMapper informationMapper;

    // 查询 Person
    @GetMapping("/search/person/{id}")
    public Result query(@PathVariable("id") String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                return Result.error("ID cannot be empty");
            }
            List<Person> list = informationMapper.getPersonById(id);
            if (list == null || list.isEmpty()) {
                return Result.error("Person not found with ID: " + id);
            }
            logger.info("Queried person with ID: {}", id);
            return Result.success(list);
        } catch (Exception e) {
            logger.error("Error querying person with ID: {}", id, e);
            return Result.error("An error occurred while querying person");
        }
    }

    // 查询 User
    @GetMapping("/search/user/{id}")
    public Result query1(@PathVariable("id") String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                return Result.error("ID cannot be empty");
            }
            List<User> list = informationMapper.getUserById(id);
            if (list == null || list.isEmpty()) {
                return Result.error("User not found with ID: " + id);
            }
            logger.info("Queried user with ID: {}", id);
            return Result.success(list);
        } catch (Exception e) {
            logger.error("Error querying user with ID: {}", id, e);
            return Result.error("An error occurred while querying user");
        }
    }

    // 查询 Counselor
    @GetMapping("/search/counselor/{id}")
    public Result query2(@PathVariable("id") String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                return Result.error("ID cannot be empty");
            }
            List<Counselor> list = informationMapper.getCounselorById(id);
            if (list == null || list.isEmpty()) {
                return Result.error("Counselor not found with ID: " + id);
            }
            logger.info("Queried counselor with ID: {}", id);
            return Result.success(list);
        } catch (Exception e) {
            logger.error("Error querying counselor with ID: {}", id, e);
            return Result.error("An error occurred while querying counselor");
        }
    }

    // 查询 Tutor
    @GetMapping("/search/tutor/{id}")
    public Result query3(@PathVariable("id") String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                return Result.error("ID cannot be empty");
            }
            List<Tutor> list = informationMapper.getTutorById(id);
            if (list == null || list.isEmpty()) {
                return Result.error("Tutor not found with ID: " + id);
            }
            logger.info("Queried tutor with ID: {}", id);
            return Result.success(list);
        } catch (Exception e) {
            logger.error("Error querying tutor with ID: {}", id, e);
            return Result.error("An error occurred while querying tutor");
        }
    }

    // 禁用用户
    @PostMapping("/delete")
    public Result bannedUser(@RequestParam("id") String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                return Result.error("ID cannot be empty");
            }
            int i = informationMapper.bannedUser(id);
            if (i <= 0) {
                return Result.error("User not found or already banned with ID: " + id);
            }
            logger.info("Banned user with ID: {}", id);
            return Result.success("User banned successfully");
        } catch (Exception e) {
            logger.error("Error banning user with ID: {}", id, e);
            return Result.error("An error occurred while banning user");
        }
    }

    // 更新用户信息
    @PostMapping("/user/update")
    public Result updatePerson(
            @RequestParam("id") String id,
            @RequestParam("role") String role,
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("gender") String gender,
            @RequestParam("age") int age,
            @RequestParam("idcard") String idcard,
            @RequestParam("phone") String phone,
            @RequestParam("selfDescription") String selfDescription) {
        try {
            if (id == null || id.trim().isEmpty()) {
                return Result.error("ID cannot be empty");
            }
            if (email == null || !email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                return Result.error("Invalid email format");
            }
            if (age < 0 || age > 150) {
                return Result.error("Invalid age");
            }
            int i = informationMapper.updatePerson(id, role, name, email, gender, age, idcard, phone, selfDescription);
            if (i <= 0) {
                return Result.error("Person not found with ID: " + id);
            }
            logger.info("Updated person with ID: {}", id);
            return Result.success("Update successful");
        } catch (Exception e) {
            logger.error("Error updating person with ID: {}", id, e);
            return Result.error("An error occurred while updating person");
        }
    }

    // 插入 Person
    @PostMapping("/insert/person")
    public Result insert(@RequestBody Person person) {
        try {
            if (person == null || person.getId() == null || person.getId().trim().isEmpty()) {
                return Result.error("Person ID cannot be empty");
            }
            int i = informationMapper.insertPerson(person);
            if (i <= 0) {
                return Result.error("Insert failed for person with ID: " + person.getId());
            }
            logger.info("Inserted person with ID: {}", person.getId());
            return Result.success("Insert successful");
        } catch (Exception e) {
            logger.error("Error inserting person with ID: {}", person != null ? person.getId() : "null", e);
            return Result.error("An error occurred while inserting person");
        }
    }

    // 插入 User
    @PostMapping("/insert/user")
    public Result insert1(@RequestBody User user) {
        try {
            if (user == null || user.getId() == null || user.getId().trim().isEmpty()) {
                return Result.error("User ID cannot be empty");
            }
            int i = informationMapper.insertUser(user);
            if (i <= 0) {
                return Result.error("Insert failed for user with ID: " + user.getId());
            }
            logger.info("Inserted user with ID: {}", user.getId());
            return Result.success("Insert successful");
        } catch (Exception e) {
            logger.error("Error inserting user with ID: {}", user != null ? user.getId() : "null", e);
            return Result.error("An error occurred while inserting user");
        }
    }

    // 插入 Counselor
    @PostMapping("/insert/counselor")
    public Result insert2(@RequestBody Counselor counselor) {
        try {
            if (counselor == null || counselor.getId() == null || counselor.getId().trim().isEmpty()) {
                return Result.error("Counselor ID cannot be empty");
            }
            int i = informationMapper.insertCounselor(counselor);
            if (i <= 0) {
                return Result.error("Insert failed for counselor with ID: " + counselor.getId());
            }
            logger.info("Inserted counselor with ID: {}", counselor.getId());
            return Result.success("Insert successful");
        } catch (Exception e) {
            logger.error("Error inserting counselor with ID: {}", counselor != null ? counselor.getId() : "null", e);
            return Result.error("An error occurred while inserting counselor");
        }
    }

    // 插入 Tutor
    @PostMapping("/insert/tutor")
    public Result insert3(@RequestBody Tutor tutor) {
        try {
            if (tutor == null || tutor.getId() == null || tutor.getId().trim().isEmpty()) {
                return Result.error("Tutor ID cannot be empty");
            }
            int i = informationMapper.insertTutor(tutor);
            if (i <= 0) {
                return Result.error("Insert failed for tutor with ID: " + tutor.getId());
            }
            logger.info("Inserted tutor with ID: {}", tutor.getId());
            return Result.success("Insert successful");
        } catch (Exception e) {
            logger.error("Error inserting tutor with ID: {}", tutor != null ? tutor.getId() : "null", e);
            return Result.error("An error occurred while inserting tutor");
        }
    }

    // 修改密码
    @PostMapping("/auth/change-password")
    public Result changePassword(@RequestParam("id") String id, @RequestParam("password") String password) {
        try {
            if (id == null || id.trim().isEmpty()) {
                return Result.error("ID cannot be empty");
            }
            if (password == null || password.length() < 6) {
                return Result.error("Password must be at least 6 characters");
            }
            int i = informationMapper.changePassword(id, password);
            if (i <= 0) {
                return Result.error("User not found with ID: " + id);
            }
            logger.info("Changed password for ID: {}", id);
            return Result.success("Password changed successfully");
        } catch (Exception e) {
            logger.error("Error changing password for ID: {}", id, e);
            return Result.error("An error occurred while changing password");
        }
    }

    // 查询最后登录时间
    @GetMapping("/person/last-login/{id}")
    public Result lastLogin(@PathVariable("id") String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                return Result.error("ID cannot be empty");
            }
            List<Person> list = informationMapper.getLastLoginTime(id);
            if (list == null || list.isEmpty()) {
                return Result.error("Person not found with ID: " + id);
            }
            logger.info("Queried last login time for ID: {}", id);
            return Result.success(list);
        } catch (Exception e) {
            logger.error("Error querying last login time for ID: {}", id, e);
            return Result.error("An error occurred while querying last login time");
        }
    }

    // 修改头像
    @PostMapping("/user/avatar")
    public Result changeAvatar(@RequestParam("idPictureLink") String idPictureLink, @RequestParam("id") String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                return Result.error("ID cannot be empty");
            }
            if (idPictureLink == null || idPictureLink.trim().isEmpty()) {
                return Result.error("Picture link cannot be empty");
            }
            int i = informationMapper.changeAvatar(id, idPictureLink);
            if (i <= 0) {
                return Result.error("User not found with ID: " + id);
            }
            logger.info("Changed avatar for ID: {}", id);
            return Result.success("Avatar changed successfully");
        } catch (Exception e) {
            logger.error("Error changing avatar for ID: {}", id, e);
            return Result.error("An error occurred while changing avatar");
        }
    }

    // 登录
    @GetMapping("/auth/login/{id}/{password}")
    public Result login(@PathVariable("id") String id, @PathVariable("password") String password) {
        try {
            if (id == null || id.trim().isEmpty()) {
                return Result.error("ID cannot be empty");
            }
            if (password == null || password.trim().isEmpty()) {
                return Result.error("Password cannot be empty");
            }
            List<Person> list = informationMapper.Login(id, password);
            if (list == null || list.isEmpty()) {
                return Result.error("Invalid ID or password");
            }
            logger.info("User logged in with ID: {}", id);
            return Result.success(list);
        } catch (Exception e) {
            logger.error("Error logging in with ID: {}", id, e);
            return Result.error("An error occurred while logging in");
        }
    }
}