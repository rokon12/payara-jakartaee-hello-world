package org.eclipse.jakarta.hello;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.List;

@Stateless
public class TaskRepository {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public Task save(Task task) {
        return em.merge(task);
    }

    public List<Task> findAll() {
        return em.createQuery("SELECT t FROM Task t", Task.class).getResultList();
    }

    public List<Task> findByStatus(Status status) {
        return em.createQuery("SELECT t FROM Task t WHERE t.status = :status", Task.class)
                .setParameter("status", status)
                .getResultList();
    }
}
