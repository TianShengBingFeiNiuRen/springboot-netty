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
public class ResponseTestVO implements Serializable {

    private String message;
    private Date date;
}
