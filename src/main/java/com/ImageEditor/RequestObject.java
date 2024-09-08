package com.ImageEditor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestObject {

    private List<String> fileImageName;

    private String latitude;

    private String longitude;

    private String latRef;

    private String lonRef;
}
