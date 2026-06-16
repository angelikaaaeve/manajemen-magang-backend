package com.bsi.manajement_magang.shared;

import java.util.List;

public class PaginatedResponse<T> extends APIResponse<List<T>> {
    private long length;
    private int index;
    private int size;
    private int totalPages;

    public PaginatedResponse() {
        super();
    }

    public PaginatedResponse(boolean success, String message, List<T> data, Object errors, long length, int index, int size, int totalPages) {
        super(success, message, data, errors);
        this.length = length;
        this.index = index;
        this.size = size;
        this.totalPages = totalPages;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public static <T> PaginatedResponse<T> success(List<T> data, long length, int index, int size, int totalPages, String message) {
        return new PaginatedResponse<>(
                true,
                message,
                data,
                null,
                length,
                index,
                size,
                totalPages
        );
    }

    public static <T> PaginatedResponse<T> success(List<T> data, long length, int index, int size, String message) {
        int totalPages = size > 0 ? (int) Math.ceil((double) length / size) : 1;
        return success(data, length, index, size, totalPages, message);
    }

    public static <T> PaginatedResponse<T> success(List<T> data, long length, int index, int size) {
        return success(data, length, index, size, "Success");
    }
}
