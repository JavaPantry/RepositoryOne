package ca.canon.fast.model.impl;

import ca.canon.fast.model.IBaseEntity;

@SuppressWarnings("serial")
public class BaseEntity implements IBaseEntity {
	private Long id = null;
	private Integer idx = 0;

	private Long version;
	public Long getVersion() {return version;}
	public void incrementVersion() {version++;}
	public void setVersion(Long version) {this.version = version;}

	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


/*
 * Hibernate will not treat entity with id==0 as new and will try to execute update against not existing record (so for new record id should be always = null)
 * @see ca.canon.fast.model.IBaseEntity#isNew()
 */
	public boolean isNew() {
		return getId() == null || getId() == 0L;
	}

	public Integer getIdx() {
		return idx;
	}

	public void setIdx(Integer idx) {
		this.idx = idx;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((idx == null) ? 0 : idx.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		BaseEntity other = (BaseEntity) obj;
		if (id == null) {
			if (other.id != null){
				return false;
			}
		} else if (!id.equals(other.id))
			return false;
		if (idx == null) {
			if (other.idx != null){
				return false;
			}
		} else if (!idx.equals(other.idx)){
			return false;
		}
		return true;
	}

}
