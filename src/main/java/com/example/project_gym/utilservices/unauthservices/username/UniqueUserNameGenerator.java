package com.example.project_gym.utilservices.unauthservices.username;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UniqueUserNameGenerator {
    private RawUserNameGenerator userNameGenerator;
    private UsernameLookupService userLookupService;

    @Autowired
    public void setUserNameGenerator(RawUserNameGenerator userNameGenerator) {
        this.userNameGenerator = userNameGenerator;
    }

    @Autowired
    public void setUserLookupService(UsernameLookupService userLookupService) {
        this.userLookupService = userLookupService;
    }


    public String generateUnique(String firstName, String lastName) {
        String base = userNameGenerator.generateUserName(firstName, lastName);

        if (!userLookupService.existsByUserName(base)) {
            return base;
        }

        int suffix = 1;
        while (true) {
            String candidate = base + suffix;
            if (!userLookupService.existsByUserName(candidate)) {
                return candidate;
            }
            suffix++;
        }
    }
}
