
package com.app.ecom.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.ecom.Repository.UserRepository;
import com.app.ecom.dto.AddressDTO;
import com.app.ecom.dto.UserRequest;
import com.app.ecom.dto.UserResponse;
import com.app.ecom.model.Address;
import com.app.ecom.model.User;
import com.app.ecom.model.UserRole;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Constructor injection
    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 1. Fetch all users
    public List<UserResponse> fetchAllUser() {

        return userRepository.findAll()
                .stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    // 2. Register a new user
    public UserResponse addUser(UserRequest request) {

        // Check whether email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());

        // Encrypt password using BCrypt
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        // Assign default customer role
        user.setRole(UserRole.CUSTOMER);

        // Convert AddressDTO into Address entity
        if (request.getAddress() != null) {

            Address address = new Address();

            address.setStreet(request.getAddress().getStreet());
            address.setCity(request.getAddress().getCity());
            address.setState(request.getAddress().getState());
            address.setZipcode(request.getAddress().getZipcode());
            address.setCountry(request.getAddress().getCountry());

            user.setAddress(address);
        }

        // Save user in database
        User savedUser = userRepository.save(user);

        return mapToUserResponse(savedUser);
    }

    // 3. Fetch user by ID
    @Transactional(readOnly = true)
    public Optional<UserResponse> fetchUser(Long id) {

        return userRepository.findById(id)
                .map(this::mapToUserResponse);
    }

    // 4. Update existing user details
    public Optional<UserResponse> updateUser(
            Long id, UserRequest request) {

        return userRepository.findById(id)
                .map(existingUser -> {

                    existingUser.setFirstName(request.getFirstName());
                    existingUser.setLastName(request.getLastName());
                    existingUser.setEmail(request.getEmail());
                    existingUser.setPhone(request.getPhone());

                    // Update address if provided
                    if (request.getAddress() != null) {

                        Address address = existingUser.getAddress();

                        if (address == null) {
                            address = new Address();
                        }

                        address.setStreet(request.getAddress().getStreet());
                        address.setCity(request.getAddress().getCity());
                        address.setState(request.getAddress().getState());
                        address.setZipcode(request.getAddress().getZipcode());
                        address.setCountry(request.getAddress().getCountry());

                        existingUser.setAddress(address);
                    }

                    // Save updated user
                    User savedUser = userRepository.save(existingUser);

                    return mapToUserResponse(savedUser);
                });
    }

    // 5. Convert User entity into UserResponse DTO
    private UserResponse mapToUserResponse(User user) {

        UserResponse response = new UserResponse();

        response.setId(String.valueOf(user.getId()));
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole());

        // Convert Address entity into AddressDTO
        if (user.getAddress() != null) {

            AddressDTO addressDTO = new AddressDTO();

            addressDTO.setStreet(user.getAddress().getStreet());
            addressDTO.setCity(user.getAddress().getCity());
            addressDTO.setState(user.getAddress().getState());
            addressDTO.setZipcode(user.getAddress().getZipcode());
            addressDTO.setCountry(user.getAddress().getCountry());

            response.setAddress(addressDTO);
        }

        return response;
    }
}
