package org.seasar.chronos.store.dao;

import java.util.List;

import org.seasar.chronos.store.entity.TaskEntity;
import org.seasar.dao.annotation.tiger.S2Dao;

@S2Dao(bean = TaskEntity.class)
public interface TaskDao {

	public int insert(TaskEntity entity);

	public int update(TaskEntity entity);

	public int delete(TaskEntity entity);

	public TaskEntity selectById(int id);

	public List<TaskEntity> selectAll();

}
