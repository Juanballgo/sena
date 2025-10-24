package Co.edu.sena.socialnet.msvc.users.repository;

import Co.edu.sena.socialnet.msvc.users.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
}
