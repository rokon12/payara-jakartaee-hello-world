package org.eclipse.jakarta.hello;

import jakarta.annotation.Resource;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.enterprise.concurrent.ManagedExecutorDefinition;
import jakarta.enterprise.concurrent.ManagedExecutorService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
@ManagedExecutorDefinition(
        name = "java:app/concurrent/taskScheduler",
        hungTaskThreshold = 120000,
        virtual = true)
public class TaskScheduler {

    private static final Logger logger = Logger.getLogger(TaskScheduler.class.getName());

    @Inject
    private TaskRepository taskRepository;

    @Resource(lookup = "java:app/concurrent/taskScheduler")
    private ManagedExecutorService taskScheduler;

    @Schedule(hour = "*", minute = "*", second = "*/10", persistent = false)
    public void pullTask() {
        List<Task> newTasks = taskRepository.findByStatus(Status.NEW);
        newTasks.forEach(this::execute);
    }

    @Transactional
    private void execute(Task task) {
        task.setStatus(Status.IN_PROGRESS);
        taskRepository.save(task);

        taskScheduler.execute(() -> processTask(task));
    }

    private void processTask(Task task) {
        try {
            String inputUrl = task.getInputUrl();
            logger.info(String.format("Fetching content from URL: %s executing on thread: %s", inputUrl, Thread.currentThread()));

            task.setMetaData(extractAllMetadata(inputUrl));
            task.setStatus(Status.COMPLETED);
            task.setCompletedAt(Instant.now());
            taskRepository.save(task);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to fetch content from URL: " + task.getInputUrl(), e);
            updateTaskStatusToFailed(task);
        }
    }

    private Map<String, String> extractAllMetadata(String inputUrl) {

        Map<String, String> metadata = new HashMap<>();
        try {
            Document doc = Jsoup.connect(inputUrl).get();
            extractMetaTags(doc, metadata);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return metadata;
    }


    private void extractMetaTags(Document doc, Map<String, String> metadata) {
        Elements metaTags = doc.getElementsByTag("meta");
        for (Element metaTag : metaTags) {
            String name = metaTag.attr("name");
            String property = metaTag.attr("property");
            String content = metaTag.attr("content");
            if (!name.isEmpty()) {
                metadata.put(name, content);
            } else if (!property.isEmpty()) {
                metadata.put(property, content);
            }
        }
    }

    @Transactional
    private void updateTaskStatusToFailed(Task task) {
        task.setStatus(Status.FAILED);
        taskRepository.save(task);
    }
}
