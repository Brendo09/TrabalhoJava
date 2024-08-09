package model.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.Viagem;
import model.ModelException;
import model.User;

public class MySqlViagem implements ViagemDAO{
	
	public boolean save(Viagem viagem)throws ModelException{
		DBHandler db = new DBHandler();
		String sqlInsert = "INSERT INTO viagem VALUES (DEFAULT, ?, ?, ?, ?, ?);";
		db.prepareStatement(sqlInsert);
		
		db.setString(1, viagem.getCidade());
		db.setString(2, viagem.getMotivo());
		db.setDate(3, viagem.getStart() == null ? new Date() : viagem.getStart());
		
		if(viagem.getEnd() == null)
			db.setNullDate(4);
		else db.setDate(4, viagem.getEnd());

		db.setInt(5, viagem.getUser().getId());
		
		return db.executeUpdate() > 0;
	}
	
	public boolean update(Viagem viagem) throws ModelException {
		DBHandler db = new DBHandler();
		
		String sqlUpdate = 
				"UPDATE viagem "
				+ " SET cidade = ?, "
				+ " motivo = ?, "
				+ " start = ?, "
				+ " end = ?, "
				+ " user_id = ? "
				+ " WHERE id = ?; "; 
		
		db.prepareStatement(sqlUpdate);
		
		db.setString(1, viagem.getCidade());
		db.setString(2, viagem.getMotivo());
		
		db.setDate(3, viagem.getStart() == null ? new Date() : viagem.getStart());
		
		if (viagem.getEnd() == null)
			db.setNullDate(4);
		else db.setDate(4, viagem.getEnd());
		
		db.setInt(5, viagem.getUser().getId());
		db.setInt(6, viagem.getId());
		
		return db.executeUpdate() > 0;
	}
	
	public boolean delete(Viagem viagem) throws ModelException {
		DBHandler db = new DBHandler();
		
		String sqlDelete = " DELETE FROM viagem "
		         + " WHERE id = ?;";

		db.prepareStatement(sqlDelete);		
		db.setInt(1, viagem.getId());
		
		return db.executeUpdate() > 0;
	}
	
	public List<Viagem> listAll() throws ModelException {
		DBHandler db = new DBHandler();
		
		List<Viagem> viagens = new ArrayList<Viagem>();
			
		// Declara uma instrução SQL
		String sqlQuery = " SELECT v.id as 'viagem_id', v.*, u.* \n"
				+ " FROM viagem v \n"
				+ " INNER JOIN users u \n"
				+ " ON v.user_id = u.id;";
		
		db.createStatement();
	
		db.executeQuery(sqlQuery);

		while (db.next()) {
			User user = new User(db.getInt("user_id"));
			user.setName(db.getString("nome"));
			user.setGender(db.getString("sexo"));
			user.setEmail(db.getString("email"));
			
			Viagem viagem = new Viagem(db.getInt("viagem_id"));
			viagem.setCidade(db.getString("cidade"));
			viagem.setMotivo(db.getString("motivo"));
			viagem.setStart(db.getDate("start"));
			viagem.setEnd(db.getDate("end"));
			viagem.setUser(user);
			
			viagens.add(viagem);
		}
		
		return viagens;
	}
	
	public Viagem findById(int id) throws ModelException {
		DBHandler db = new DBHandler();
		
		String sql = "SELECT * FROM viagem WHERE id = ?;";
		
		db.prepareStatement(sql);
		db.setInt(1, id);
		db.executeQuery();
		
		Viagem v = null;
		while (db.next()) {
			v = new Viagem(id);
			v.setCidade(db.getString("cidade"));
			v.setMotivo(db.getString("motivo"));
			v.setStart(db.getDate("start"));
			v.setEnd(db.getDate("end"));
			
			UserDAO userDAO = DAOFactory.createDAO(UserDAO.class); 
			User user = userDAO.findById(db.getInt("user_id"));
			v.setUser(user);
			
			break;
		}
		
		return v;
	}
}
	
