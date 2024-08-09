package model.dao;

import java.util.List;

import model.Viagem;
import model.ModelException;

public interface ViagemDAO {

	boolean save(Viagem viagem) throws ModelException;
	boolean update(Viagem viagem) throws ModelException;
	boolean delete(Viagem viagem) throws ModelException;
	List<Viagem> listAll() throws ModelException;
	Viagem findById(int id) throws ModelException;


}
