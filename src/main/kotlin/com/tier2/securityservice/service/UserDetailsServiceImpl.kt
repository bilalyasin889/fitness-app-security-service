package com.tier2.securityservice.service

import com.tier2.securityservice.entity.mapToUserDetails
import com.tier2.securityservice.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserDetailsServiceImpl(
    private val userRepository: UserRepository
) : UserDetailsService {

    @Transactional(readOnly = true)
    override fun loadUserByUsername(username: String): UserDetails =
        userRepository.findUserByEmail(username)
            ?.mapToUserDetails()
            ?: throw UsernameNotFoundException("Username not found!")
}