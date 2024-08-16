package antifraud;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppUserService {

    private final AppUserRepository repository;

    private final ModelMapper modelMapper;

    public AppUserService(AppUserRepository repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    public AppUserDTO findAppUserDTOByUsername(String username) {
        AppUser appuser = repository.findAppUserByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Not found!")
        );
        return convertAppUserToDTO(appuser);

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

    private AppUserDTO convertAppUserToDTO(AppUser appUser) {
        return modelMapper.map(appUser, AppUserDTO.class);
    }



}
