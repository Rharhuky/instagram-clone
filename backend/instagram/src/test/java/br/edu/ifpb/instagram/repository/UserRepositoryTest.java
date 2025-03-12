package br.edu.ifpb.instagram.repository;

import br.edu.ifpb.instagram.model.entity.UserEntity;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@ActiveProfiles("it")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void findById_WithNoExistingUserEntity_ReturnsEmpty() {
        var id = 1L;
        var sut = userRepository.findById(id);
        assertThat(sut).isEmpty();
    }

    @Test
    void findById_WithExistingUserEntity_ReturnsUserEntity() {
        var userEntity = new UserEntity();
        userEntity.setEmail("rharhuky@mail.com.br");
        userEntity.setFullName("Rharhuandrew User");
        userEntity.setEncryptedPassword("pass");
        userEntity.setUsername("Rharhuky");

        testEntityManager.persistFlushFind(userEntity);
        testEntityManager.detach(userEntity);

        var sut = userRepository.findById(userEntity.getId());
        assertThat(sut).isNotEmpty();
        assertThat(sut.get().getEmail()).isEqualTo(userEntity.getEmail());
    }

    @Test
    void save_WithValidUserEntity_ReturnsUserEntityOptional(){
        var userEntity = new UserEntity();
        userEntity.setEmail("rharhuky@mail.com.br");
        userEntity.setFullName("Rharhuandrew User");
        userEntity.setEncryptedPassword("pass");
        userEntity.setUsername("Rharhuky");

        var newUserEntity = userRepository.save(userEntity);
        var sut = testEntityManager.find(UserEntity.class, newUserEntity.getId());

        assertThat(sut).isNotNull();
        assertThat(sut.getEmail()).isEqualTo(newUserEntity.getEmail());
    }

    @Test
    void save_WithUserEntityWithoutEmail_ThrowsException(){
        var userEntity = new UserEntity();
        userEntity.setFullName("Rharhuandrew User");
        userEntity.setEncryptedPassword("pass");
        userEntity.setUsername("Rharhuky");
        assertThatCode(() -> userRepository.save(userEntity))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("not-null property references a null or transient value: br.edu.ifpb.instagram.model.entity.UserEntity.email");
    }

    @Test
    void save_WithTwoUsuarioEntityWithSameEmail_ThrowsException(){
        var userEntityA = new UserEntity();
        userEntityA.setEmail("rharhuandrew@mail.com.br");
        userEntityA.setFullName("Rharhuandrew User");
        userEntityA.setEncryptedPassword("pass");
        userEntityA.setUsername("Rharhuky");

        var userEntityB = new UserEntity();
        userEntityB.setEmail("rharhuandrew@mail.com.br");
        userEntityB.setFullName("Rharhuandrew UserB");
        userEntityB.setEncryptedPassword("pass");
        userEntityB.setUsername("Rharhuky Allex");

        testEntityManager.persistFlushFind(userEntityA);
        assertThatCode(() -> testEntityManager.persistFlushFind(userEntityB)).isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("Duplicate entry '%s'", userEntityA.getEmail());
    }

    @Test
    void updatePartialUser_WithValidData_ReturnNumberOfModifiedRows() {
        var user = new UserEntity();
        user.setEmail("rharhuandrew@mail.com.br");
        user.setFullName("Rharhuandrew UserB");
        user.setEncryptedPassword("pass");
        user.setUsername("Rharhuky Allex");
        var theUser = testEntityManager.persistFlushFind(user);

        var sut = userRepository.updatePartialUser("Rharhuandrew", "", "", "", user.getId());

        assertThat(sut).isNotZero();
        assertThat(theUser.getId()).isEqualTo(user.getId());
    }

    @Test
    void updatePartialUser_WithNoExistingUser_ReturnsZero() {
        var sut = userRepository.updatePartialUser("Rharhuandrew", "", "", "", 10l);
        var noExistingUsuarioEntity = testEntityManager.find(UserEntity.class, 10l);

        assertThat(sut).isZero();
        assertThat(noExistingUsuarioEntity).isNull();
    }

    @Test
    void updatePartialUser_WithExistingEmail_ThrowsException() {
        var userA = new UserEntity();
        userA.setEmail("rharhuandrew@mail.com.br");
        userA.setFullName("Rharhuandrew UserA");
        userA.setEncryptedPassword("pass");
        userA.setUsername("Rharhuky");

        var userB = new UserEntity();
        userB.setEmail("rharhuandrew2@mail.com.br");
        userB.setFullName("Rharhuandrew UserB");
        userB.setEncryptedPassword("pass");
        userB.setUsername("Rharhuky Allex");

        testEntityManager.persistFlushFind(userA);
        testEntityManager.persistFlushFind(userB);

        assertThatCode(() -> userRepository.updatePartialUser(
                "", userA.getEmail(),
                "",
                "",
                userB.getId())).isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("Duplicate entry '%s'", userA.getEmail());
    }

    @Test
    void delete_WithExistingUserEntity_DeleteUsuario() {
        var userA = new UserEntity();
        userA.setEmail("rharhuandrew@mail.com.br");
        userA.setFullName("Rharhuandrew UserA");
        userA.setEncryptedPassword("pass");
        userA.setUsername("Rharhuky");
        testEntityManager.persistFlushFind(userA);

        userRepository.deleteById(userA.getId());
        assertThatCode(() ->
                testEntityManager.find(UserEntity.class,userA.getId()))
                .isNull();
    }
}
