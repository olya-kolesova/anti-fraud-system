package antifraud.service;

import antifraud.utils.AppUserAdapter;
import antifraud.dto.AppUserDTO;
import antifraud.entity.AppUser;
import antifraud.repository.AppUserRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppUserService implements UserDetailsService {

    private final AppUserRepository repository;
    private final ModelMapper modelMapper;

    public AppUserService(AppUserRepository repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    public AppUser findAppUserByUsername(String username) {
        return repository.findAppUserByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Not found!")
        );
    }

    public boolean isUserPresent(String username) {
        return repository.findAppUserByUsername(username).isPresent();
    }

    public void saveUser(AppUser user) {
        repository.save(user);
    }


    public List<AppUserDTO> findAppUserDTOByOrder() {
       return repository.findByOrderById().stream()
               .map(this::convertAppUserToDTO)
               .collect(Collectors.toList());
    }

    @Transactional
    public void deleteAppUserByUsername(String username) {
        repository.deleteByUsername(username);
    }

    public AppUserDTO convertAppUserToDTO(AppUser appUser) {
        AppUserDTO userDTO = modelMapper.map(appUser, AppUserDTO.class);
        userDTO.setRole(appUser.getAuthority());
        return userDTO;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = repository
                .findAppUserByUsername(username.toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("Not found!"));

        return new AppUserAdapter(appUser);
    }
}
