package Co.edu.sena.socialnet.msvc.users.repository;
import org.springframework.data.mongodb.repository.MongoRepository;
import Co.edu.sena.socialnet.msvc.users.model.Rol;

public interface RolRepository extends MongoRepository<Rol, String> {

}
