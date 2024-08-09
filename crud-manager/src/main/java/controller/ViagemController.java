package controller;

import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Company;
import model.ModelException;
import model.User;
import model.Viagem;
import model.dao.CompanyDAO;
import model.dao.DAOFactory;
import model.dao.UserDAO;
import model.dao.ViagemDAO;

@WebServlet(urlPatterns = {"/viagens", "/viagem/form","/viagem/insert", "/viagem/delete","/viagem/update"})

public class ViagemController extends HttpServlet{
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		
		String action = req.getRequestURI();
		
		switch (action) {
		case "/crud-manager/viagem/form": {
			CommonsController.listUsers(req);
			req.setAttribute("action", "insert");
			ControllerUtil.forward(req, resp, "/form-viagem.jsp");			
			break;
		}
		case "/crud-manager/viagem/update": {
			listViagens(req);
			Viagem viagem = loadViagem(req);
			req.setAttribute("user", viagem);
			req.setAttribute("action", "update");
			ControllerUtil.forward(req, resp, "/form-viagem.jsp");
			break;
		}
		default:
			listViagens(req);
			
			ControllerUtil.transferSessionMessagesToRequest(req);
		
			ControllerUtil.forward(req, resp, "/viagem.jsp");
		}
	}
	
	private void listViagens(HttpServletRequest req) {
		ViagemDAO dao = DAOFactory.createDAO(ViagemDAO.class);
		
		List<Viagem> viagens = null;
		try {
			viagens = dao.listAll();
		} catch (ModelException e) {
			// Log no servidor
			e.printStackTrace();
		}
		
		if (viagens != null)
			req.setAttribute("viagem", viagens);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		
		String action = req.getRequestURI();
		
		switch (action) {
		case "/crud-manager/viagem/insert": {
			insertViagem(req, resp);			
			break;
		}
		case "/crud-manager/viagem/delete" :{
			
			deleteViagem(req, resp);
			
			break;
		}
		case "/crud-manager/viagem/update" :{
			updateViagem(req, resp);
			
			break;
		}
		default:
			System.out.println("URL inválida " + action);
		}
		
		ControllerUtil.redirect(resp, req.getContextPath() + "/viagens");
	}

	private void updateViagem(HttpServletRequest req, HttpServletResponse resp) {

		String viagemCidade = req.getParameter("cidade");
		String motivo = req.getParameter("motivo");
		String start = req.getParameter("start");
		String end = req.getParameter("end");
		Integer userId = Integer.parseInt(req.getParameter("user"));
		
		Viagem via = new Viagem();
		via.setCidade(viagemCidade);
		via.setMotivo(motivo);
		via.setStart(ControllerUtil.formatDate(start));
		via.setEnd(ControllerUtil.formatDate(end));
		via.setUser(new User(userId));
		
		ViagemDAO dao = DAOFactory.createDAO(ViagemDAO.class);
	
		try {
			if (dao.update(via)) {
				ControllerUtil.sucessMessage(req, "Viagem para'" + via.getCidade() + "' atualizado com sucesso.");
			}
			else {
				ControllerUtil.errorMessage(req, "Viagem para '" + via.getCidade()
				+ "' não pode ser atualizado.");
			}
		} catch (ModelException e) {
			// log no servidor
			e.printStackTrace();
			ControllerUtil.errorMessage(req, e.getMessage());
		}
	
		
	
	}

	private void deleteViagem(HttpServletRequest req, HttpServletResponse resp) {

		String viagemIdParameter = req.getParameter("id");
		
		int viagemId = Integer.parseInt(viagemIdParameter);
		
		ViagemDAO dao = DAOFactory.createDAO(ViagemDAO.class);
		
		try {
			Viagem viagem = dao.findById(viagemId);
			
			if (viagem == null)
				throw new ModelException("Empresa não encontrada para deleção.");
			
			if (dao.delete(viagem)) {
				ControllerUtil.sucessMessage(req, "Viagem '" + 
						viagem.getCidade() + "' deletada com sucesso.");
			}
			else {
				ControllerUtil.errorMessage(req, "Viagem '" + 
						viagem.getCidade() + "' não pode ser deletado. "
								+ "Há dados relacionados à Usuarios.");
			}
		} catch (ModelException e) {
			// log no servidor
			if (e.getCause() instanceof 
					SQLIntegrityConstraintViolationException) {
				ControllerUtil.errorMessage(req, e.getMessage());
			}
			e.printStackTrace();
			ControllerUtil.errorMessage(req, e.getMessage());
		}
	
	}

	private void insertViagem(HttpServletRequest req, HttpServletResponse resp) {

		String viagemCidade = req.getParameter("cidade");
		String motivo = req.getParameter("motivo");
		String start = req.getParameter("start");
		String end = req.getParameter("end");
		Integer userId = Integer.parseInt(req.getParameter("user"));
		
		Viagem via = new Viagem();
		via.setCidade(viagemCidade);
		via.setMotivo(motivo);
		via.setStart(ControllerUtil.formatDate(start));
		via.setEnd(ControllerUtil.formatDate(end));
		via.setUser(new User(userId));
		
		ViagemDAO dao = DAOFactory.createDAO(ViagemDAO.class);
	
		try {
			if (dao.save(via)) {
				ControllerUtil.sucessMessage(req, "Viagem para'" + via.getCidade() 
				+ "' salva com sucesso.");
			}
			else {
				ControllerUtil.errorMessage(req, "Viagem para '" + via.getCidade()
				+ "' não pode ser salva.");
			}
		} catch (ModelException e) {
			// log no servidor
			e.printStackTrace();
			ControllerUtil.errorMessage(req, e.getMessage());
		}
	
		
	}
	
	private Viagem loadViagem(HttpServletRequest req) {
		String viagemIdParameter = req.getParameter("viagemId");
		
		int viagemId = Integer.parseInt(viagemIdParameter);
		
		ViagemDAO dao = DAOFactory.createDAO(ViagemDAO.class);
		
		try {
			Viagem viagem = dao.findById(viagemId);
			
			if (viagem == null)
				throw new ModelException("Usuário não encontrado para alteração");
			
			return viagem;
		} catch (ModelException e) {
			// log no servidor
			e.printStackTrace();
			ControllerUtil.errorMessage(req, e.getMessage());
		}
		
		return null;
	}
	

}
