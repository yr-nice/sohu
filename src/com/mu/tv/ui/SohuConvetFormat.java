/*
 * Cctv_uiAboutBox.java
 */

package com.mu.tv.ui;

import com.mu.tv.media.Converter;
import java.io.File;
import javax.swing.JOptionPane;
import org.jdesktop.application.Action;

public class SohuConvetFormat extends javax.swing.JDialog {

    public SohuConvetFormat(java.awt.Frame parent) {
        super(parent);
        initComponents();
        getRootPane().setDefaultButton(btnConvert);
    }

    @Action public void closeAboutBox() {
        dispose();
    }

    private static Converter converter;
    static{
        converter = new Converter();
        converter.setDaemon(true);
        converter.start();

    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        tfInputDir = new javax.swing.JTextField();
        tfOutputDir = new javax.swing.JTextField();
        btnConvert = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        tfQuality = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(SohuConvetFormat.class);
        setTitle(resourceMap.getString("title")); // NOI18N
        setModal(true);
        setName("aboutBox"); // NOI18N
        setResizable(false);

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        tfInputDir.setText(resourceMap.getString("tfInputDir.text")); // NOI18N
        tfInputDir.setName("tfInputDir"); // NOI18N
        tfInputDir.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tfInputDirFocusLost(evt);
            }
        });

        tfOutputDir.setText(resourceMap.getString("tfOutputDir.text")); // NOI18N
        tfOutputDir.setName("tfOutputDir"); // NOI18N

        btnConvert.setText(resourceMap.getString("btnConvert.text")); // NOI18N
        btnConvert.setName("btnConvert"); // NOI18N
        btnConvert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConvertActionPerformed(evt);
            }
        });

        btnClose.setText(resourceMap.getString("btnClose.text")); // NOI18N
        btnClose.setName("btnClose"); // NOI18N
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        tfQuality.setText(resourceMap.getString("tfQuality.text")); // NOI18N
        tfQuality.setName("tfQuality"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tfOutputDir, javax.swing.GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE)
                            .addComponent(tfInputDir, javax.swing.GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(btnConvert)
                                .addGap(26, 26, 26)
                                .addComponent(btnClose)))
                        .addContainerGap(79, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(tfQuality, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(tfInputDir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(tfOutputDir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(tfQuality, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(82, 82, 82)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnClose)
                    .addComponent(btnConvert))
                .addGap(37, 37, 37))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnCloseActionPerformed
    {//GEN-HEADEREND:event_btnCloseActionPerformed
        dispose();
    }//GEN-LAST:event_btnCloseActionPerformed

    private void btnConvertActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnConvertActionPerformed
    {//GEN-HEADEREND:event_btnConvertActionPerformed
        String in = tfInputDir.getText().trim();
        String out = tfOutputDir.getText().trim();

        if(in.isEmpty() || out.isEmpty())
        {
            JOptionPane.showMessageDialog(this, "Pls complete Input and Output Dir first!");
        }
        else
        {
            File fOut = new File(out);
            if(!fOut.exists())
                fOut.mkdirs();
            converter.addMonitorDir(in, out, tfQuality.getText(), false);
        }
        dispose();
    }//GEN-LAST:event_btnConvertActionPerformed

    private void tfInputDirFocusLost(java.awt.event.FocusEvent evt)//GEN-FIRST:event_tfInputDirFocusLost
    {//GEN-HEADEREND:event_tfInputDirFocusLost
        if(tfOutputDir.getText().trim().isEmpty())
        {
            tfOutputDir.setText(tfInputDir.getText());
        }
    }//GEN-LAST:event_tfInputDirFocusLost
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnConvert;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField tfInputDir;
    private javax.swing.JTextField tfOutputDir;
    private javax.swing.JTextField tfQuality;
    // End of variables declaration//GEN-END:variables
    
}