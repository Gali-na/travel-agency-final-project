package com.epam.travel_agency_final_project.service;

import com.epam.travel_agency_final_project.dto.*;
import com.epam.travel_agency_final_project.entity.User;
import com.epam.travel_agency_final_project.entity.UserTour;
import com.epam.travel_agency_final_project.entity.UserTranslation;
import com.epam.travel_agency_final_project.exeption.UserAlreadyExistsException;
import com.epam.travel_agency_final_project.mapper.TourMapper;
import com.epam.travel_agency_final_project.mapper.UserProfileMapper;
import com.epam.travel_agency_final_project.mapper.UserSecurityMapper;
import com.epam.travel_agency_final_project.model.StatusTour;
import com.epam.travel_agency_final_project.repository.UserRepository;
import com.epam.travel_agency_final_project.repository.UserTourRepository;
import com.epam.travel_agency_final_project.repository.UserTranslationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserSecurityMapper userSecurityMapper;
    private final UserTranslationRepository   userTranslationRepository;
    private final TourMapper tourMapper; // Ваш мапер для Tour -> TourDTO
    private final UserTourRepository userTourRepository;
    private final UserProfileMapper userProfileMapper;


   // Page<User> findByEmailExact(@Param("email") String email, Pageable pageable);
   @Transactional
   @Override
   public void lockUser(UUID id) {
       userRepository.lockUserById(id);
   }
    @Override
    public Page<UserProfileDTO> findAll(Pageable pageable) {
        // Використовуємо екземпляр мапера: userProfileMapper.toDto
        return userRepository.findAll(pageable).map(userProfileMapper::toDTO);
    }

    @Override
    public Page<UserProfileDTO> findByEmailExact(String email, Pageable pageable) {
        // Використовуємо той самий метод мапера
        return userRepository.findByEmailExact(email, pageable).map(userProfileMapper::toDTO);
    }



    public boolean authenticate(String email, String rawPassword) {
        return userRepository.findByEmail(email)
                .map(user -> passwordEncoder.matches(rawPassword, user.getPasswordHash())) // Порівнюємо хеші
                .orElse(false);
    }

    @Transactional
    public  BigDecimal increaseBalance(UUID userId, BigDecimal amount) {
        int updatedRows = userRepository.depositBalanceById(userId, amount);

        return userRepository.getBalanceById(userId);
    }

    @Transactional
    @Override
    public void registerUser(UserRegistrationDTO dto, String lang) {
        // 1. Перевірка: чи існує вже користувач з таким email
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("error.user.exists");
        }

        // 2. Самостійно генеруємо UUID
        UUID userId = UUID.randomUUID();

        // 3. Створюємо користувача
        User user = new User();
        user.setId(userId);
        user.setEmail(dto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setBalance(BigDecimal.ZERO);
        user.setLocked(false);

        // 4. Створюємо переклад
        UserTranslation translation = new UserTranslation();
        translation.setFirstName(dto.getFirstName());
        translation.setLastName(dto.getLastName());
        translation.setUser(user);

        // Встановлюємо ID для складеного ключа
        UserTranslation.UserTranslationId id = new UserTranslation.UserTranslationId();
        id.setUserId(userId);
        id.setLang(lang);
        translation.setId(id);

        user.setTranslations(List.of(translation));

        // 5. Зберігаємо
        userRepository.save(user);
    }

    public UserSecurityDTO findById(UUID id) {
        return  userSecurityMapper.toSecurityDto(userRepository.findById(id).orElse(null));

    }

    public boolean isExistUser(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean isBlockUser(UUID id) {
        return userRepository.findById(id)
                .map(User::isLocked)
                .orElse(true); // Якщо користувача немає, вважаємо заблокованим
    }

    @Transactional
    public void blockUser(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            user.setLocked(true);
            userRepository.save(user);
        });
    }

    // Метод для пошуку по email (ваш логін)
    public UserSecurityDTO findByEmail(String email) {
        return userSecurityMapper.toSecurityDto(userRepository.findByEmail(email).orElse(null));
    }
    @Transactional
    public void finalizePurchase(UserSecurityDTO  userSecurityDTO, TourFullDTO tourDTO) {
        // Оновлюємо баланс
        userSecurityDTO.setBalance(userSecurityDTO.getBalance().subtract(tourDTO.getPrice()));
        userRepository.save(userSecurityMapper.toEntity(userSecurityDTO));

        // Записуємо в user_tours
        UserTour userTour = new UserTour();
        userTour.setUser(userSecurityMapper.toEntity(userSecurityDTO));
        userTour.setTour(tourMapper.toEntity(tourDTO));
        userTour.setStatus(String.valueOf(StatusTour.PAID));
        userTourRepository.save(userTour);
    }

    @Transactional
   public UUID registerNewUser(UserRegistrationDTO dto) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(dto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setLocked(false);
        user.setBalance(BigDecimal.ZERO);

        // Зберігаємо юзера, щоб отримати його ID (якщо воно генерується)
        User savedUser = userRepository.save(user);

        // 2. Створюємо запис перекладу (при реєстрації зазвичай це мова за замовчуванням)
        UserTranslation translation = new UserTranslation();
        UserTranslation.UserTranslationId id = new UserTranslation.UserTranslationId(user.getId(), "uk");
        translation.setId(id);
        translation.setUser(user); // Важливо для JPA
        translation.setFirstName(dto.getFirstName());
        translation.setLastName(dto.getLastName());
        userTranslationRepository.save(translation);

        return savedUser.getId();
    }

    public UserProfileDTO getProfileData(UUID userId, String lang) {
        // 1. Знаходимо користувача за ID
        // Використовуємо orElseThrow для коректної обробки відсутності користувача
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Користувача з ID " + userId + " не знайдено"));

        // 2. Отримуємо переклад імені та прізвища для поточної мови
        UserTranslation trans = user.getTranslations().stream()
                .filter(t -> t.getId() != null && t.getId().getLang().equalsIgnoreCase(lang))
                .findFirst()
                .orElse(new UserTranslation());

        // 3. Мапимо список бронювань (UserTour -> UserTourDTO)
        List<UserTourDTO> tourDTOs = user.getUserTours().stream()
                .map(ut -> new UserTourDTO(
                        ut.getId(),                 // bookingId
                        ut.getStatus(),             // статус бронювання
                        ut.getCreatedAt(),          // дата створення
                        tourMapper.toDto(ut.getTour(), lang) // конвертуємо тур через мапер
                ))
                .collect(Collectors.toList());

        // 4. Формуємо та повертаємо фінальний DTO
        return new UserProfileDTO(
                user.getId(),
                user.getEmail(),
                user.getBalance(),
                trans.getFirstName(),
                trans.getLastName(),
                user.isLocked(),
                tourDTOs
        );
    }
}

