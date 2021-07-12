package com.rhyme.utils;

import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

public class Utils {

    public static String getContentFromCaret(Editor editor, PsiFile file){
        int offset = editor.getCaretModel().getOffset();
        PsiElement candidateA = file.findElementAt(offset);
        PsiElement candidateB = file.findElementAt(offset - 1);
        PsiElement candidateC = file.findElementAt(offset + 1);
        if(candidateA!=null)return candidateA.getText();
        if(candidateB!=null)return candidateB.getText();
        if(candidateC!=null)return candidateC.getText();
        return null;
    }
}
