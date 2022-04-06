package com.example.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.dto.CartProductsDto;
import com.example.dto.UserLoginDto;
import com.example.entity.Cart;
import com.example.entity.User;
import com.example.entity.UserProducts;
import com.example.exception.UserException;
import com.example.exception.UserRegistrationException;
import com.example.repository.GenericCartRepo;
import com.example.repository.GenericProductReopository;
import com.example.repository.GenericUserProductRepo;
import com.example.repository.GenericUserRepository;


@Service
@Transactional
public class UserServiceImple implements UserService {
	
	@Autowired
	private GenericUserProductRepo genUserproductsRepo;
	
	@Autowired
	private GenericUserRepository genUserRepo;
	
	@Autowired
	private GenericCartRepo genCartRepo;
	
	@Autowired
	private GenericProductReopository genProductRepo;
	

	@Override
	public User addUser(User u) {
		
		try
		{
			u.setPassword(((Integer)u.getPassword().hashCode()).toString());
			
			return genUserRepo.save(u);
		}
		catch (Exception e){
			
			throw new UserRegistrationException("Already registered!");
			
		}
		
	}
	
	@Override
	public UserLoginDto authenticateUser(UserLoginDto uld) {
		User u= null;
		uld.setPassword(((Integer)uld.getPassword().hashCode()).toString());

		UserLoginDto userDetails = new UserLoginDto();
		try {
			if((u = genUserRepo.findByEmailAndPassword(uld.getEmail(),uld.getPassword()))!=null) {
				
				userDetails.setRoleId(u.getRole());
				userDetails.setUserId(u.getUserId());
				userDetails.setStatus(true);
				return userDetails;
			}
			else 
			{
				throw new UserException("User Not found!! Please try again.");
			}
		 
		}
		catch(Exception e) {
			e.printStackTrace();
			throw new UserException("User Not found!! Please try again.");
		}
		
	}

	@Override
	public User getUserById(int id) {
		
		try {
			return genUserRepo.findById(id).get();
		}catch(Exception e)
		{
			throw new UserException("User with id not found");
		}
		
	}

	@Override
	public UserProducts addtoCartProduct(CartProductsDto p) {
		UserProducts cartProduct;
		try {
			User u = getUserById(p.getUserid());
			Cart c = genCartRepo.findByUser(u);
			
			 cartProduct  = new UserProducts();
			
			cartProduct.setCart(c);
			
			cartProduct.setProduct(genProductRepo.findById(p.getProductid()).get());
			
			cartProduct.setQuantity(p.getQuantity());
			
			cartProduct.setVisiblity("Pending");
			
		return	genUserproductsRepo.save(cartProduct);
			
			
		}
		catch(Exception e){
			throw new UserException("Cart");
		}
		
	}
}
