package com.labmate.riddlebox.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.labmate.riddlebox.security.PrincipalDetails;

public class SecurityUtils {

    public static Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof PrincipalDetails) {
            PrincipalDetails principalDetails = (PrincipalDetails) auth.getPrincipal();
            return principalDetails.getUserPK();
        }
        return null; // or throw an exception if user is not found
    }

    public static String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof PrincipalDetails) {
            PrincipalDetails principalDetails = (PrincipalDetails) auth.getPrincipal();
            return principalDetails.getName();
        }
        return null; // or throw an exception if user is not found
    }

}
