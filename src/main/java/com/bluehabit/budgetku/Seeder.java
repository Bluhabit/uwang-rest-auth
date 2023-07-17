/*
 * Copyright © 2023 Blue Habit.
 *
 * Unauthorized copying, publishing of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.bluehabit.budgetku;

import com.bluehabit.budgetku.common.Constant;
import com.bluehabit.budgetku.component.role.entity.Permission;
import com.bluehabit.budgetku.component.role.entity.PermissionGroup;
import com.bluehabit.budgetku.component.user.entity.UserCredential;
import com.bluehabit.budgetku.component.user.entity.UserProfile;
import com.bluehabit.budgetku.component.role.repo.PermissionGroupRepository;
import com.bluehabit.budgetku.component.role.repo.PermissionRepository;
import com.bluehabit.budgetku.component.user.repo.UserCredentialRepository;
import com.bluehabit.budgetku.component.user.repo.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class Seeder implements ApplicationRunner {

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private PermissionGroupRepository permissionGroupRepository;
    @Autowired
    private UserCredentialRepository userCredentialRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;


    private final OffsetDateTime date = OffsetDateTime.now();
    private final LocalDate dateOfBirth = LocalDate.of(1998, 9, 16);
    private final List<Permission> permission = List.of(
            new Permission(
                    "f56917a2-279c-4ad3-8db2-8fc1e54e2beo",
                    "Send,Edit,Delete Notification",
                    Constant.WRITE_NOTIFICATION,
                    Constant.GROUP_NOTIFICATION,
                    date,
                    date,
                    false
            ),
            new Permission(
                    "da8bb576-6966-4fb1-88d7-11f0258389al",
                    "Read only notification",
                    Constant.READ_NOTIFICATION,
                    Constant.GROUP_NOTIFICATION,
                    date,
                    date,
                    false
            ),
            new Permission(
                    "f56917a2-279c-4ad3-8db2-8fc1e54e2be3",
                    "Manage User",
                    Constant.WRITE_USER,
                    Constant.GROUP_USER,
                    date,
                    date,
                    false
            ),
            new Permission(
                    "152e22bf-f2b7-4788-8f54-8b951341bd85",
                    "Manage User",
                    Constant.READ_USER,
                    Constant.GROUP_USER,
                    date,
                    date,
                    false
            ),
            new Permission(
                    "08f6e968-a60a-4072-9752-b78fcb9ca736",
                    "Role",
                    Constant.WRITE_ROLE,
                    Constant.GROUP_ROLE,
                    date,
                    date,
                    false
            ),
            new Permission(
                    "85f177bb-668f-4bc8-ab67-a181eae0fd4f",
                    "Role",
                    Constant.READ_ROLE,
                    Constant.GROUP_ROLE,
                    date,
                    date,
                    false
            ),
            new Permission(
                    "f56917a2-279c-4ad3-8db2-8fc1e54e2beo",
                    "Category",
                    Constant.WRITE_CATEGORY,
                    Constant.GROUP_CATEGORY,
                    date,
                    date,
                    false
            ),
            new Permission(
                    "e4a6b866-e5cf-4b34-b4fa-10323865b149",
                    "Category",
                    Constant.READ_CATEGORY,
                    Constant.GROUP_CATEGORY,
                    date,
                    date,
                    false
            )

    );


    @Override
    public void run(ApplicationArguments args) throws Exception {


        var password = bCryptPasswordEncoder.encode("12345678");
        var email = "admin@bluehabit.com";
        var email2 = "trian@bluehabit.com";
        var idRole = "26ff6c62-a447-4e7f-941e-e3c866bd69bc";

        permissionRepository.saveAll(permission);
        List<Permission> permission = new ArrayList<>();

        permissionRepository.findAll().forEach(permission::add);

        if (userCredentialRepository.findByUserEmail(email).isEmpty()) {
            String userId1 = "26ff6c62-a447-4e7f-941e-e3c866bd69bc";

            UserCredential user1 = new UserCredential(
                    userId1,
                    email,
                    password,
                    "ACTIVE",
                    "BASIC",
                    "sdfgfiytui",
                    date,
                    date
            );

            UserProfile profile = new UserProfile(
                    userId1,
                    "Trian",
                    dateOfBirth,
                    "081226809435",
                    "ID",
                    "https://",
                    date,
                    date
            );

            userCredentialRepository.save(user1);
            userProfileRepository.save(profile);

            user1.setUserPermission(permission);
            user1.setUserProfile(profile);
            userCredentialRepository.save(
                    user1
            );
        }

        if(!permissionGroupRepository.existsByRoleIdIgnoreCase((idRole))){
            var role = new PermissionGroup(
                    idRole,
                    "ADMIN",
                    "Buat Admin",
                    permission,
                    date,
                    date
            );
            var saved =  permissionGroupRepository.save(role);

            saved.setRolePermission(permission);
            permissionGroupRepository.save(saved);
        }



    }
}
