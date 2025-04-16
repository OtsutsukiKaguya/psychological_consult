//package com.counseling.platform.repositories;
package com.example.demo.repositories;

//import com.counseling.platform.models.User;
import com.example.demo.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户存储库接口
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 根据用户名查找用户
     */
    User findByUsername(String username);

    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 根据角色查找用户
     */
    List<User> findByRole(User.UserRole role);

    /**
     * 根据状态查找用户
     */
    List<User> findByStatus(User.UserStatus status);

    /**
     * 根据督导ID查找咨询师
     */
    @Query("SELECT u FROM User u WHERE u.supervisor.id = :supervisorId")
    List<User> findBySupervisorId(@Param("supervisorId") Long supervisorId);

    /**
     * 根据角色和状态查找用户
     */
    List<User> findByRoleAndStatus(User.UserRole role, User.UserStatus status);

    /**
     * 根据用户名模糊查询
     */
    @Query("SELECT u FROM User u WHERE u.username LIKE %:keyword% OR u.nickname LIKE %:keyword%")
    List<User> findByKeyword(@Param("keyword") String keyword);

    /**
     * 查找没有督导的咨询师
     */
    @Query("SELECT u FROM User u WHERE u.role = 'COUNSELOR' AND u.supervisor IS NULL")
    List<User> findCounselorsWithoutSupervisor();

    /**
     * 查找在线的咨询师
     */
    @Query("SELECT u FROM User u WHERE u.role = 'COUNSELOR' AND u.status = 'ONLINE'")
    List<User> findOnlineCounselors();
}