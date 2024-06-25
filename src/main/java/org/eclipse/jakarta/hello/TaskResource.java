package org.eclipse.jakarta.hello;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.List;
import java.util.logging.Logger;

@Path("/tasks")
public class TaskResource {
    private final Logger logger = Logger.getLogger(TaskResource.class.getName());

    private final TaskRepository taskRepository;

    @Inject
    public TaskResource(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTask(@Valid TaskDTO taskDTO) {
        logger.info("TaskResource.createTask() called with taskDTO: " + taskDTO);

        Task task = new Task();
        task.setInputUrl(taskDTO.getUrl());
        Task savedTask = taskRepository.save(task);

        return Response.created(URI.create("/tasks/" + savedTask.getTaskId())).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Task> getAllTasks(@QueryParam("status") Status status) {
        return taskRepository.findByStatus(status);
    }
}