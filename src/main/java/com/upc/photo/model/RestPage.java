package com.upc.photo.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author: waiter
 * @Date: 2019/4/15 22:20
 * @Version 1.0
 */
@Data
@JSONType(ignores = "pageable")
public class RestPage<T> implements Page<T> {
    private List<T> content;

    private long total;

    private int size;

    private int page;

    public <U> RestPage(List<? extends U> convertedContent, Pageable pageable, long total) {

        this.total = pageable.toOptional().filter(it -> !content.isEmpty())
                .filter(it -> it.getOffset() + it.getPageSize() > total)
                .map(it -> it.getOffset() + content.size())
                .orElse(total);
    }

    public RestPage() {
    }

    public RestPage(Page<T> page) {
        this.total = page.getTotalElements();
        this.size = page.getSize();
        this.page = page.getNumber();
        this.content  = page.getContent();
    }

    @Override
    public int getTotalPages() {
        return getSize() == 0 ? 1 : (int) Math.ceil((double) total / (double) getSize());
    }

    @Override
    public long getTotalElements() {
        return total;
    }


    @JSONField(serialize = false,deserialize = false)
    @Override
    public <U> Page<U> map(Function<? super T, ? extends U> converter) {
        return new RestPage<>(getConvertedContent(converter), getPageable(), total);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.domain.Slice#hasNext()
     */
    @JSONField(serialize = false,deserialize = false)
    @Override
    public boolean hasNext() {
        return getNumber() + 1 < getTotalPages();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.domain.Slice#getNumber()
     */
    @Override
    public int getNumber() {
        return page;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.domain.Slice#getSize()
     */
    @Override
    public int getSize() {
        return size;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.domain.Slice#getNumberOfElements()
     */
    @JSONField(serialize = false,deserialize = false)
    @Override
    public int getNumberOfElements() {
        return content.size();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.domain.Slice#hasPrevious()
     */
    @JSONField(serialize = false,deserialize = false)
    @Override
    public boolean hasPrevious() {
        return getNumber() > 0;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.domain.Slice#isFirst()
     */
    @JSONField(serialize = false,deserialize = false)
    @Override
    public boolean isFirst() {
        return !hasPrevious();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.domain.Slice#isLast()
     */
    @JSONField(serialize = false,deserialize = false)
    @Override
    public boolean isLast() {
        return !hasNext();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.domain.Slice#nextPageable()
     */
    @JSONField(serialize = false,deserialize = false)
    @Override
    public Pageable nextPageable() {
        return  Pageable.unpaged();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.domain.Slice#previousPageable()
     */
    @JSONField(serialize = false,deserialize = false)
    @Override
    public Pageable previousPageable() {
        return  Pageable.unpaged();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.domain.Slice#hasContent()
     */
    @JSONField(serialize = false,deserialize = false)
    @Override
    public boolean hasContent() {
        return !content.isEmpty();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.domain.Slice#getContent()
     */
    @Override
    public List<T> getContent() {
        return content==null?null:Collections.unmodifiableList(content);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.domain.Slice#getSort()
     */
    @JSONField(serialize = false,deserialize = false)
    @Override
    public Sort getSort() {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Iterable#iterator()
     */

    public Iterator<T> iterator() {
        return content.iterator();
    }

    /**
     *
     *
     * @param converter must not be {@literal null}.
     * @return
     */
    protected <U> List<U> getConvertedContent(Function<? super T, ? extends U> converter) {

        Assert.notNull(converter, "Function must not be null!");

        return this.stream().map(converter).collect(Collectors.toList());
    }


    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        String contentType = "UNKNOWN";
        List<T> content = getContent();

        if (content.size() > 0) {
            contentType = content.get(0).getClass().getName();
        }

        return String.format("Page %s of %d containing %s instances", getNumber() + 1, getTotalPages(), contentType);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(@Nullable Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof RestPage<?>)) {
            return false;
        }

        RestPage<?> that = (RestPage<?>) obj;

        return this.total == that.total && super.equals(obj);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

        int result = 17;

        result += 31 * (int) (total ^ total >>> 32);
        result += 31 * super.hashCode();

        return result;
    }
}
