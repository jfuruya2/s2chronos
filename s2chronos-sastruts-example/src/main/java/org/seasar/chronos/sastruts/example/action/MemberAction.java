package org.seasar.chronos.sastruts.example.action;

import java.util.List;

import javax.annotation.Resource;

import org.seasar.chronos.sastruts.example.entity.User;
import org.seasar.chronos.sastruts.example.form.MemberForm;
import org.seasar.chronos.sastruts.example.task.SessionTimerTask;
import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.framework.beans.util.BeanMap;
import org.seasar.framework.beans.util.Beans;
import org.seasar.framework.log.Logger;
import org.seasar.framework.util.tiger.CollectionsUtil;
import org.seasar.struts.annotation.ActionForm;
import org.seasar.struts.annotation.Execute;

public class MemberAction {
	private final Logger LOG = Logger.getLogger(MemberAction.class);
	@Resource
	protected JdbcManager jdbcManager;

	@ActionForm
	@Resource
	protected MemberForm memberForm;

	public List<BeanMap> userItems = CollectionsUtil.newArrayList();

	public List<BeanMap> userStatusItems = CollectionsUtil.newArrayList();

	@Execute(validator = false, removeActionForm = true)
	public String index() {
		memberForm.initialize();
		buildUserStatusItems();
		List<User> userList = this.jdbcManager.from(User.class).orderBy(
				"userId").getResultList();
		for (User user : userList) {
			userItems.add(Beans.createAndCopy(BeanMap.class, user).execute());
		}
		return "index.html";
	}

	@Execute(validator = false)
	public String register() {
		buildUserStatusItems();
		return "register.html";
	}

	@Execute(validator = false)
	public String edit() {
		buildUserStatusItems();
		User user = this.jdbcManager.from(User.class).id(
				this.memberForm.targetUserId).getSingleResult();
		Beans.copy(user, memberForm).execute();
		return "register.html";
	}

	private void buildUserStatusItems() {
		BeanMap bm = new BeanMap();
		bm.put("value", Integer.valueOf(0));
		bm.put("label", "無効");
		userStatusItems.add(bm);
		bm = new BeanMap();
		bm.put("value", Integer.valueOf(1));
		bm.put("label", "有効");
		userStatusItems.add(bm);
		bm = new BeanMap();
		bm.put("value", Integer.valueOf(2));
		bm.put("label", "一時停止");
		userStatusItems.add(bm);
	}
	
	public SessionTimerTask sessionTimerTask;

	@Execute(input = "register.html", removeActionForm = true, redirect=true)
	public String submit() {
		if (memberForm.userId == null) {
			User user = Beans.createAndCopy(User.class, memberForm).execute();
			user.userStatus = User.STATUS_ENABLE;
			user.versionNo = 0L;
			jdbcManager.insert(user).execute();
		} else {
			User user = Beans.createAndCopy(User.class, memberForm).execute();
			jdbcManager.update(user).excludesNull().execute();
		}
		sessionTimerTask.testName = "xyz";
		LOG.debug(">>>>testName = xyz!!!, class="+sessionTimerTask);
		return "../member/";
	}

	@Execute(validator = false, removeActionForm = true, redirect=true)
	public String delete() {
		this.jdbcManager.updateBySql("DELETE FROM USER WHERE USER_ID = ? ",
				Long.class).params(this.memberForm.targetUserId).execute();
		return "../member/";
	}

}
