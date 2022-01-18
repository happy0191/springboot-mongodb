package in.bushansirgur.springbootmongodb.service;

import in.bushansirgur.springbootmongodb.exception.TodoCollectionException;
import in.bushansirgur.springbootmongodb.model.TodoDTO;
import in.bushansirgur.springbootmongodb.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TodoServiceImpl implements TodoService{

    @Autowired
    private TodoRepository todoRepo;

    @Override
    public void createTodo(TodoDTO todo) throws ConstraintViolationException, TodoCollectionException {
        Optional<TodoDTO> todoOptional = todoRepo.findByTodo(todo.getTodo());
        if (todoOptional.isPresent()){
            throw new TodoCollectionException(TodoCollectionException.TodoAlreadyExists());
        }else {
            todo.setCreateAt(new Date(System.currentTimeMillis()));
            todoRepo.save(todo);
        }
    }

    @Override
    public List<TodoDTO> getAllTodos() {
        List<TodoDTO> todos = todoRepo.findAll();
        if (todos.size() > 0){
            return todos;
        }else {
            return new ArrayList<TodoDTO>();
        }
    }

    @Override
    public TodoDTO getSingleTodo(String id) throws TodoCollectionException {
       Optional<TodoDTO> optionalTodoDTO = todoRepo.findById(id);
       if (!optionalTodoDTO.isPresent()) {
           throw new TodoCollectionException(TodoCollectionException.NotFoundExcepiton(id));
       }else{
           return optionalTodoDTO.get();
       }
    }

    @Override
    public void updateTodo(String id, TodoDTO todo) throws TodoCollectionException {
        Optional<TodoDTO> todoWithId = todoRepo.findById(id);
        Optional<TodoDTO> todoWithSameName = todoRepo.findByTodo(todo.getTodo() );
        if (todoWithId.isPresent()){

            if (todoWithSameName.isPresent() && !todoWithSameName.get().getId().equals(id)){
                throw new TodoCollectionException(TodoCollectionException.TodoAlreadyExists());
            }

            TodoDTO todoToUpdate = todoWithId.get();
            todoToUpdate.setTodo(todo.getTodo());
            todoToUpdate.setDescription(todo.getDescription());
            todoToUpdate.setCompleted(todo.getCompleted());
            todoToUpdate.setUpdateAt(new Date(System.currentTimeMillis()));
            todoRepo.save(todoToUpdate);
        }else{
            throw  new TodoCollectionException(TodoCollectionException.NotFoundExcepiton(id));
        }
    }

    @Override
    public void deleteTodoById(String id) throws TodoCollectionException {
        Optional<TodoDTO> todoOptional = todoRepo.findById(id);
        if (!todoOptional.isPresent()){
            throw new TodoCollectionException(TodoCollectionException.NotFoundExcepiton(id));
        }else {
            todoRepo.deleteById(id);
        }
    }
}
