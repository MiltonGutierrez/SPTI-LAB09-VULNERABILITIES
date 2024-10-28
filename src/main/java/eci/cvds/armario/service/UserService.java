package eci.cvds.armario.service;

import eci.cvds.armario.repository.UserRepository;
import eci.cvds.armario.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void adicionar(User user) {
        userRepository.save(user);
    }

    public boolean validarUsuario(User logInUser) {
        if (userRepository.findByUsername(logInUser.getUsername()) == null) {
            return false;
        }
        User user = userRepository.findByUsername(logInUser.getUsername());
        return user.getUsername().equals(logInUser.getUsername()) &&
                user.getPassword().equals(logInUser.getPassword());
    }

    public User actualizar(String id, User updatedUser) {
        User user = userRepository.findById(id).get();
        user.setPassword(updatedUser.getPassword());
        user.setRole(updatedUser.getRole());
        user.setUsername(updatedUser.getUsername());
        return userRepository.save(user);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User getUserById(String id) throws ClassNotFoundException {
        Optional<User> user = userRepository.findById(id);
        if (!user.isEmpty())
            return user.get();
        else{
            throw new ClassNotFoundException();
        }
    }


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void eliminarUsuario(String id) {
        userRepository.deleteById(id);
    }
}
