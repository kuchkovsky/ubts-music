package ua.org.ubts.songs.service.impl;

import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;
import ua.org.ubts.songs.entity.RoleEntity;
import ua.org.ubts.songs.entity.UserEntity;
import ua.org.ubts.songs.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Primary
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
		List<GrantedAuthority> authorities = new ArrayList<>();
		for (RoleEntity roleEntity : user.getRoles()) {
			authorities.add(new SimpleGrantedAuthority(roleEntity.getName()));
		}
		return new org.springframework.security.core.userdetails.User(user.getEmail(),
				user.getPassword(), authorities);
	}

}
