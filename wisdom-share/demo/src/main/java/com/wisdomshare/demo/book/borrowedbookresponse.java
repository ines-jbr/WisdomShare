package com.wisdomshare.demo.book;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class borrowedbookresponse {

    private Integer id;
    private String title;
    private String authorName;
    private String isbn;
    private Double rate; // Double au lieu de double (meilleure gestion null)
    private boolean returned;
    private boolean returnApproved;
}