package com.andon.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Andon
 * 2022/7/28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestTestVO implements Serializable {

    private String key;
    private String value;
    private Date date;
}
