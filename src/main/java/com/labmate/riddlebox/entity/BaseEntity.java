package com.labmate.riddlebox.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


//@EntityListeners(AuditingEntityListener.class) //애너테이션은 엔티티의 생명 주기 이벤트를 처리하기 위한 리스너를 지정 (orm.xml로 전역적으로 지정)
@MappedSuperclass //해당 클래스가 데이터베이스 테이블과 직접적으로 매핑되지 않음을 나타냄
                  //대신, 이 클래스를 상속받는 자식 클래스들이 부모 클래스의 매핑 정보를 상속받아 사용할 수 있음
@Getter
public class BaseEntity extends  BaseTimeEntity {

    @CreatedBy  //애너테이션은 엔티티가 생성될 때 해당 엔티티의 생성자(작성자) 정보를 저장하는 필드에 붙임
                //AuditorAware 인터페이스의 구현을 통해 현재 로그인한 사용자의 정보가 이 필드에 자동으로 설정됨
    @Column(updatable = false) //해당 컬럼의 값을 업데이트 할 수 없음
    private Long createdBy;

    @LastModifiedBy // 엔티티가 마지막으로 수정될 때 해당 엔티티를 수정한 사용자의 정보를 저장하는 필드에 사용
    private Long lastModifiedBy;

}
