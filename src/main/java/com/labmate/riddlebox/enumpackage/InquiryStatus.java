package com.labmate.riddlebox.enumpackage;

public enum InquiryStatus {
    ANSWERING("답변중"),
    COMPLETED("답변완료"),
    TEMP_DELETED("임시삭제"), //잠깐 숨겨야 할 때
    DELETED("삭제");

    private final String description;

    InquiryStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
