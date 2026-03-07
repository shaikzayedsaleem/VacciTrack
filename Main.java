package com.vaccination;

import com.vaccination.ui.LoginFrame;
import javax.swing.SwingUtilities;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 *  VaxDrive – Vaccination Drive Management System
 *  Mullathim IbK College of Engineering and Technology
 *  Department of Computer Science and Engineering
 *  MCA Industry Ready Program (IA-407) | AY 2023–24 IV Semester
 *  Java Programming Module – Week 5 Final Project
 * ╚══════════════════════════════════════════════════════════════╝
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
