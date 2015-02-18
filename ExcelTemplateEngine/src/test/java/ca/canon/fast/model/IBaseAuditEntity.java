package ca.canon.fast.model;

import java.util.Date;

public interface IBaseAuditEntity extends IBaseEntity {

	public Date getCreateOn();
	public void setCreateOn(Date createOn);
	public String getCreateBy();
	public void setCreateBy(String createBy);
	public Date getUpdateOn();
	public void setUpdateOn(Date updateOn);
	public String getUpdateBy();
	public void setUpdateBy(String updateBy);
	public IBaseAuditEntity updateAuditInfo(String userName);

}