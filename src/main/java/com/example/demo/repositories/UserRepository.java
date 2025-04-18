////package com.counseling.platform.repositories;
//package com.example.demo.repositories;
//
////import com.counseling.platform.models.User;
//import com.example.demo.models.User;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
///**
// * 用户存储库接口
// */
//@Repository
//public interface UserRepository extends JpaRepository<User, Long> {
//
//    /**
//     * 根据用户名查找用户
//     */
//    User findByUsername(String username);
//
//    /**
//     * 检查用户名是否存在
//     */
//    boolean existsByUsername(String username);
//
//    /**
//     * 根据角色查找用户
//     */
//    List<User> findByRole(User.UserRole role);
//
//    /**
//     * 根据状态查找用户
//     */
//    List<User> findByStatus(User.UserStatus status);
//
//    /**
//     * 根据督导ID查找咨询师
//     */
//    @Query("SELECT u FROM User u WHERE u.supervisor.id = :supervisorId")
//    List<User> findBySupervisorId(@Param("supervisorId") Long supervisorId);
//
//    /**
//     * 根据角色和状态查找用户
//     */
//    List<User> findByRoleAndStatus(User.UserRole role, User.UserStatus status);
//
//    /**
//     * 根据用户名模糊查询
//     */
//    @Query("SELECT u FROM User u WHERE u.username LIKE %:keyword% OR u.nickname LIKE %:keyword%")
//    List<User> findByKeyword(@Param("keyword") String keyword);
//
//    /**
//     * 查找没有督导的咨询师
//     */
//    @Query("SELECT u FROM User u WHERE u.role = 'COUNSELOR' AND u.supervisor IS NULL")
//    List<User> findCounselorsWithoutSupervisor();
//
//    /**
//     * 查找在线的咨询师
//     */
//    @Query("SELECT u FROM User u WHERE u.role = 'COUNSELOR' AND u.status = 'ONLINE'")
//    List<User> findOnlineCounselors();
//}

//package com.example.demo.repositories;
//
//import com.example.demo.models.User;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
///**
// * 用户数据访问接口
// */
//@Repository
//public interface UserRepository extends JpaRepository<User, String> {
//
//    /**
//     * 根据邮箱查找用户
//     */
//    User findByEmail(String email);
//
//    /**
//     * 判断某邮箱是否已存在
//     */
//    boolean existsByEmail(String email);
//
//    /**
//     * 根据角色查询用户
//     */
//    List<User> findByRole(User.UserRole role);
//
//    /**
//     * 根据状态查询用户（如 ONLINE）
//     */
//    List<User> findByState(User.UserStatus state);
//
//    /**
//     * 根据角色 + 状态联合查询用户
//     */
//    List<User> findByRoleAndState(User.UserRole role, User.UserStatus state);
//}

package com.example.demo.repositories;

import com.example.demo.models.User;
import com.example.demo.models.User.UserRole;
import com.example.demo.models.User.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    /**
     * JpaRepository 已经内置 findById(String id)
     * 所以无需再定义 findById
     */

    /**
     * 检查用户是否存在
     */
    boolean existsById(String id);

    /**
     * 根据角色查找用户
     */
    List<User> findByRole(UserRole role);

    /**
     * 根据状态查找用户
     */
    List<User> findByState(UserStatus state);

    /**
     * 根据角色和状态查找用户
     */
    List<User> findByRoleAndState(UserRole role, UserStatus state);

    /**
     * 模糊查找姓名或邮箱
     */
    @Query("SELECT u FROM User u WHERE u.name LIKE %:keyword% OR u.email LIKE %:keyword%")
    List<User> findByKeyword(@Param("keyword") String keyword);

    /**
     * 查找在线的咨询师
     */
    @Query("SELECT u FROM User u WHERE u.role = 'COUNSELOR' AND u.state = 'ONLINE'")
    List<User> findOnlineCounselors();
}
