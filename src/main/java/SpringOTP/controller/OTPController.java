package SpringOTP.controller;

import java.security.Security;
import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import SpringOTP.model.EmailTemplate;
import SpringOTP.service.EmailService;
import SpringOTP.service.OTPService;

@Controller
public class OTPController {

	@Autowired
	public OTPService otpService;

	@Autowired
	public EmailService emailService;

	@GetMapping("/generatedOtp")
	public String generatedOTP() throws MessagingException {

		Authentication auth=SecurityContextHolder.getContext().getAuthentication();

		String username = auth.getName();
		int otp=otpService.generatedOTP(username);
		//Generate The Template to send OTP

		EmailTemplate template = new  EmailTemplate("SendOtp.html");
		Map<String,String> replacements = new HashMap<String,String>();

		replacements.put("user", username);
		replacements.put("otpnum",String.valueOf(otp));
		String message = template.getTemplate(replacements);
		emailService.sendOtpMessage("Logged in users EmailAddress", "OTP -springBoot", message);

		return "otppage";
	}

	@RequestMapping(value = "/validateOtp",method = RequestMethod.GET)
	public @ResponseBody String validateOtp(@RequestParam("otpnum") int otpnum) {

		final String SUCCESS = "Entered Otp is Not valid.Please Retry";

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username= auth.getName();

		//valid the otp
		if(otpnum >=0 ) {

			int serverOtp = otpService.getOtp(username);
			if(serverOtp > 0) {
				if(otpnum == serverOtp) {
					otpService.cleanOTP(username);
					
					return ("Entered Otp is valid");
				}
				else {
					return SUCCESS;
				} 
			}else {
					return "FAIL";
				}	
		}
		else {
			return "FAIL";
		}
	}
}

