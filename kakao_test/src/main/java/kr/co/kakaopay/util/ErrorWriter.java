package kr.co.kakaopay.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.springframework.stereotype.Component;

@Component
public class ErrorWriter {
	public String getMessage(Exception e) {
		 
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        return errors.toString();
	}
}
