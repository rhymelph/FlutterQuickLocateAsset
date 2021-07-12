package com.rhyme.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.rhyme.utils.Utils;
import org.apache.http.util.TextUtils;

public class JumpToAsset extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        final Project currentProject = e.getProject();
        assert (currentProject != null);
        String selectedText = getString(e);
        if (selectedText == null) return;

        if (selectedText.contains("\"") || selectedText.contains("\'")) {
            selectedText = selectedText.replaceAll("\"", "").replaceAll("\'", "");
        }
        String[] filePathList = selectedText.split("/");
        if (filePathList.length > 0) {
            String selectedFileName = filePathList[filePathList.length - 1];
            PsiFile[] files = FilenameIndex.getFilesByName(currentProject, selectedFileName, GlobalSearchScope.allScope(currentProject));
            if (files.length == 1) {
                //查找到这个文件了
                final VirtualFile file = files[0].getVirtualFile();
                OpenFileDescriptor openFileDescriptor = new OpenFileDescriptor(currentProject, file);
                openFileDescriptor.navigate(true);
            } else {
                for (PsiFile file : files) {
                    String path = file.getVirtualFile().getPath();
                    String projectName = currentProject.getName();
                    path = path.split(projectName)[1].substring(1);
                    if (path.equals(selectedText)) {
                        OpenFileDescriptor openFileDescriptor = new OpenFileDescriptor(currentProject, file.getVirtualFile());
                        openFileDescriptor.navigate(true);
                        return;
                    }
                }
                Messages.showMessageDialog(currentProject, "Asset file not found.", e.getPresentation().getText(), Messages.getInformationIcon());
            }
        } else {
            Messages.showMessageDialog(currentProject, "Asset file not found.", e.getPresentation().getText(), Messages.getInformationIcon());
        }
    }

//    @Override
//    public void update(@NotNull AnActionEvent e) {
//        super.update(e);
//        e.getPresentation().setEnabledAndVisible(canShow(e));
//    }


    private boolean canShow(AnActionEvent e) {
        final Project currentProject = e.getProject();
        if (currentProject == null) return false;
        final Editor mEditor = e.getData(PlatformDataKeys.EDITOR);
        final PsiFile mFile = e.getData(PlatformDataKeys.PSI_FILE);
        if (mEditor == null) return false;
        if (mFile == null) return false;
        String selectedText = Utils.getContentFromCaret(mEditor, mFile);
        if (TextUtils.isEmpty(selectedText)) return false;
        return selectedText.contains("\"") || selectedText.contains("\'");
    }

    private String getString(AnActionEvent e) {
        final Project currentProject = e.getProject();
        if (currentProject == null) return null;

        final Editor mEditor = e.getData(PlatformDataKeys.EDITOR);
        if (mEditor == null) return null;
        final PsiFile mFile = e.getData(PlatformDataKeys.PSI_FILE);
        if (mFile == null) return null;

        return Utils.getContentFromCaret(mEditor, mFile);
    }

}
