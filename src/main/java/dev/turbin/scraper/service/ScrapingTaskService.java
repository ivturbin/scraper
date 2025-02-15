package dev.turbin.scraper.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import dev.turbin.scraper.repository.ScrapingTaskRepository;
import dev.turbin.scraper.entity.ScrapingTaskEntity;
import dev.turbin.scraper.enums.ScrapingTaskStatusEnum;
import dev.turbin.scraper.enums.ScrapingTaskTypeEnum;

@Service
@RequiredArgsConstructor
public class ScrapingTaskService {

    private final ScrapingTaskRepository scrapingTaskRepository;

    public ScrapingTaskEntity startScrapingTask(ScrapingTaskTypeEnum taskTypeEnum) {
        ScrapingTaskEntity scrapingTaskEntity = new ScrapingTaskEntity();
        scrapingTaskEntity.setTaskType(taskTypeEnum.name());
        scrapingTaskEntity.setTaskStatus(ScrapingTaskStatusEnum.RUNNING.name());
        scrapingTaskEntity.setPassed(0);
        scrapingTaskEntity.setFailed(0);

        Long taskId = scrapingTaskRepository.save(scrapingTaskEntity);
        scrapingTaskEntity.setScrapingTaskId(taskId);

        return scrapingTaskEntity;
    }

    public void endScrapingTask(ScrapingTaskEntity scrapingTaskEntity) {
        if (ScrapingTaskStatusEnum.RUNNING.name()
                .equals(scrapingTaskEntity.getTaskStatus())) {
            scrapingTaskEntity.setTaskStatus(ScrapingTaskStatusEnum.SUCCEED.name());
            scrapingTaskRepository.endTask(scrapingTaskEntity);
        } else {
            scrapingTaskRepository.endTask(scrapingTaskEntity);
        }
    }

    public void updateSucceed(ScrapingTaskEntity scrapingTaskEntity) {
        scrapingTaskEntity.setPassed(scrapingTaskEntity.getPassed() + 1);
        scrapingTaskRepository.updateTask(scrapingTaskEntity);
    }

    public void updateFailed(ScrapingTaskEntity scrapingTaskEntity) {
        scrapingTaskEntity.setPassed(scrapingTaskEntity.getPassed() + 1);
        scrapingTaskEntity.setFailed(scrapingTaskEntity.getFailed() + 1);
        scrapingTaskRepository.updateTask(scrapingTaskEntity);
    }
}
