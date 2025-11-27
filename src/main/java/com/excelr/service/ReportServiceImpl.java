package com.excelr.service;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import com.excelr.entity.CitizenPlan;

import com.excelr.repository.CitizenPlanRepo;
import com.excelr.request.SearchRequest;
import com.excelr.util.EmailUtil;
import com.excelr.util.ExcelGenerator;
import com.excelr.util.PdfGenerator;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class ReportServiceImpl implements ReportService{
	
	@Autowired
	private CitizenPlanRepo planRepo;
	
	@Autowired
	private ExcelGenerator excelgenerator;
	
	@Autowired
	private PdfGenerator pdfGenerator;
	
	@Autowired
	private EmailUtil emailUtil;
	
	@Override
	public List<String> getPlanNames(){
		return planRepo.getPlanNames();
	}
	
	@Override
	public List<String> getPlanStatus(){
		return planRepo.getPlanStatus();
	}
	
	@Override
	public List<CitizenPlan> search(SearchRequest request)
	{
		CitizenPlan entity=new CitizenPlan();
		
		if(null!=request.getPlanName() && !"".equals(request.getPlanName())) {
			entity.setPlanName(request.getPlanName());
		}
		if(null!=request.getPlanStatus() && !"".equals(request.getPlanStatus())) {
			entity.setPlanStatus(request.getPlanStatus());
		}
		if(null!=request.getGender() && !"".equals(request.getGender())) {
			entity.setGender(request.getGender());
		}
		// if it is not null and not empty
		if(null!=request.getStartDate() && !"".equals(request.getStartDate())) {
			String startDate = request.getStartDate();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			//covert string to localdate
			LocalDate localDate = LocalDate.parse(startDate, formatter);
			entity.setPlanStartDate(localDate);
		}
		
		if(null!=request.getEndDate() && !"".equals(request.getEndDate())) {
			String endDate = request.getEndDate();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			//covert string to localdate
			LocalDate localDate = LocalDate.parse(endDate, formatter);
			entity.setPlanEndDate(localDate);
		}
		return planRepo.findAll(Example.of(entity));
		
	}
	
	@Override
	public boolean exportExcel(HttpServletResponse response) throws Exception{
		File f=new File ("plans.xls");
		List<CitizenPlan> plans=planRepo.findAll();
		excelgenerator.generate(response, plans, f);
		

		String subject = "Test mail subject";
		String body = "<h1>Test mail body </h1>";
		String to = "rajeshallada@gmail.com";
		
		
		emailUtil.sendEmail(subject, body, to, f);
		
		f.delete();
		return true;
	}
	
	@Override
	public boolean exportPdf(HttpServletResponse response) throws Exception{
		
        File f = new File("Plans.pdf");		
		List<CitizenPlan> plans = planRepo.findAll();
		pdfGenerator.generate(response, plans, f);
		
		String subject = "Test mail subject";
		String body = "<h1>Test mail body </h1>";
		String to = "rajeshallada@gmail.com";
		
		emailUtil.sendEmail(subject, body, to, f);

		f.delete();
		return true;
	}
	
	
	
	

}
