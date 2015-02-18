package ca.canon.fast.model.impl;

import java.util.Date;

import ca.canon.fast.model.IBaseAuditEntity;

public class BaseAuditEntity extends BaseEntity implements IBaseAuditEntity {
	private static final long serialVersionUID = 1L;

	//transient dirty flag (only to indicate change presence in data object coming from UI see "records[i].data.dirty = records[i].dirty;" in /app/controller/sales/SalesForecastController.js)
	protected boolean	dirty = false;

	/**
	 * EGU
	 */
	protected Date createOn;
	protected String createBy;
	protected Date updateOn;
	protected String updateBy;

	
	public Date getCreateOn() {return createOn;}
	public void setCreateOn(Date createOn) {this.createOn = createOn;}
	public String getCreateBy() {return createBy;}
	public void setCreateBy(String createBy) {this.createBy = createBy;}
	public Date getUpdateOn() {return updateOn;}
	public void setUpdateOn(Date updateOn) {this.updateOn = updateOn;}
	public String getUpdateBy() {return updateBy;}
	public void setUpdateBy(String updateBy) {this.updateBy = updateBy;}
	public IBaseAuditEntity updateAuditInfo(String userName) {
		if (this.isNew()){
			this.setCreateBy(userName);
			this.setCreateOn(new Date());
		}
		this.setUpdateOn(new Date());
		this.setUpdateBy(userName);
		return this;
	}

	//transient dirty flag (only to indicate change presence in data object coming from UI see "records[i].data.dirty = records[i].dirty;" in /app/controller/sales/SalesForecastController.js)
	// see implementation fast-service/src/ca/canon/fast/model/impl/BaseAuditEntity.java
	public boolean isDirty() {return dirty;}
	public void setDirty(boolean dirty) {this.dirty = dirty;}

}
