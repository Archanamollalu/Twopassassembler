import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import javax.swing.border.*;
import java.io.*;
import java.io.*;
import java.util.*;

class Twopassassembler {
    private static JTextArea txtArea;
    private static File inputFile = new File("input.txt");
    private static File optabFile = new File("optab.txt");
    private static File symtabFile = new File("symtab.txt");
    private static File intermediateFile = new File("intermediate.txt");
    private static File objcode = new File("objectcode.txt");
    private static String op="ADD-18\nAND-40\nCOMP-28\nDIV-24\nJ-3C\nJEQ-30\nJGT-34\nJLT-38\nJSUB-48\nLDA-00\nLDCH-50\nLDL-08\nLDX-0\nMUL-20\nOR-44\nRD-D8\nRSUB-4C\nSTA-0C\nSTCH-54\nSTL-14\nSTSW-E8\nSTX-10\nSUB-1C\nTD-E0\nTIX-2C\nWD-DC\nEND-*";
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Twopassassembler::new);
    }

    public Twopassassembler() {
        JFrame fr = new JFrame("Assembler");
        fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fr.setSize(1300, 750);
        fr.setLayout(null);
        fr.getContentPane().setBackground(new Color(119,66,67));

        JLabel inLabel = new JLabel("Input Source Code");
        inLabel.setBounds(50, 1, 250, 50);
        inLabel.setForeground(new Color(255,255,255));
        fr.add(inLabel);
        JTextArea inputCodeArea = new JTextArea();
        inputCodeArea.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
        inputCodeArea.setBackground(new Color(135,255,186));
        inputCodeArea.setForeground(new Color(0,0,0));
        inputCodeArea.setCaretColor(Color.WHITE); 
        inputCodeArea.setBounds(50, 46, 350, 500);
        fr.add( inputCodeArea);

        JLabel sLabel = new JLabel("SymbTab");
        sLabel.setBounds(490, 1, 250, 50);
        sLabel.setForeground(new Color(255,255,255));
        fr.add(sLabel);
        JTextArea symCodeArea = new JTextArea();
        symCodeArea.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
        symCodeArea.setBackground(new Color(135,255,186));
        symCodeArea.setForeground(new Color(0,0,0));
        symCodeArea.setCaretColor(Color.WHITE); 
        symCodeArea.setBounds(490, 46, 350, 350);
        fr.add(symCodeArea);  

        JLabel tLabel = new JLabel("Intermediate Code");
        tLabel.setBounds(910, 1, 250, 50);
        tLabel.setForeground(new Color(255,255,255));
        fr.add(tLabel);
        JTextArea intCodeArea = new JTextArea();
        intCodeArea.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
        intCodeArea.setBackground(new Color(135,255,186));
        intCodeArea.setForeground(new Color(0, 0, 0));
        intCodeArea.setCaretColor(Color.WHITE); 
        intCodeArea.setBounds(910, 46, 350, 350);
        fr.add(intCodeArea);  

        JLabel outLabel = new JLabel("Object Code");
        outLabel.setBounds(490, 390, 250, 40);
        outLabel.setForeground(new Color(255,255,255));
        fr.add(outLabel);
        JTextArea objectCodeArea = new JTextArea();
        objectCodeArea.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
        objectCodeArea.setBounds(490,420, 770, 250);
        objectCodeArea.setBackground(new Color(135,255,186));
        objectCodeArea.setForeground(new Color(0, 0,0));
        fr.add(objectCodeArea);

        JButton asmblButton = new JButton("Assemble");
        asmblButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
        asmblButton.setBounds(150, 600, 210, 40);
        asmblButton.setBackground(new Color(245,238,67));
        asmblButton.setForeground(new Color(0, 0, 0));
        fr.add(asmblButton);
        
        asmblButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try (BufferedWriter inputWriter = new BufferedWriter(new FileWriter(inputFile))) {
                    String inputCode = inputCodeArea.getText();
                    if(inputCode==null){
                    objectCodeArea.setText(" ");
                    }
                    inputWriter.write(inputCode);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                try (BufferedWriter opcodeWriter = new BufferedWriter(new FileWriter(optabFile))) {
                     opcodeWriter.write(op+" ");
                 } catch (IOException ex) {
                    ex.printStackTrace();
                }
                try{
                String label, opcode, operand, code, mnemonic;
                label = opcode = operand = code = mnemonic = "";
                int programLength = passOne(label, opcode, operand, code, mnemonic);
                passTwo(label, opcode, operand, code, mnemonic, programLength);
                } catch (IOException d){
                    d.printStackTrace();
                }
                try (BufferedReader reader = new BufferedReader(new FileReader(objcode))) {
                    StringBuilder fileContent = new StringBuilder();
                    String objcode;

                    while ((objcode = reader.readLine()) != null) {
                        fileContent.append(objcode).append(System.lineSeparator());
                    }

                    objectCodeArea.setText(fileContent.toString());
                } catch (IOException f) {
                    f.printStackTrace();
                }
                try (BufferedReader reader = new BufferedReader(new FileReader(intermediateFile))) {
                        StringBuilder content = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            content.append(line).append("\n");
                        }
                        intCodeArea.setText(content.toString());
                    } catch (IOException ex) {
                        intCodeArea.setText("Error reading file: " + ex.getMessage());
                }
                try (BufferedReader reader = new BufferedReader(new FileReader(symtabFile))) {
                        StringBuilder content = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            content.append(line).append("\n");
                        }
                        symCodeArea.setText(content.toString());
                    } catch (IOException ex) {
                        symCodeArea.setText("Error reading file: " + ex.getMessage());
                }

            }
        });

        fr.setVisible(true);
    }

    private void showTextFile(File file, String title, JFrame parent) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            txtArea.setText(content.toString());
        } catch (IOException ex) {
            txtArea.setText("Error reading file: " + ex.getMessage());
        }   
    }

  public static int passOne(String label, String opcode, String operand, String code, String mnemonic) throws IOException {
    int locctr = 0, start = 0, length;
    boolean found;
    try{
        Scanner inputScanner = new Scanner(inputFile);
        BufferedWriter symtabWriter = new BufferedWriter(new FileWriter(symtabFile, false));
        BufferedWriter intermediateWriter = new BufferedWriter(new FileWriter(intermediateFile, false));

        label = inputScanner.next();
        opcode = inputScanner.next();
        operand = inputScanner.next();

        if (opcode.equals("START")) {
            start = Integer.parseInt(operand, 16); 
            locctr = start;
            intermediateWriter.write(label + "\t" + opcode + "\t" + operand + "\n");

            if (inputScanner.hasNext()) {
                label = inputScanner.next();
                opcode = inputScanner.next();
                operand = inputScanner.next();
            }
        }
        while (!opcode.equals("END")) {
            intermediateWriter.write(Integer.toHexString(locctr).toUpperCase() + "\t" + label + "\t" + opcode + "\t" + operand + "\n");

            if (!label.equals("**")) {
                symtabWriter.write(label + "\t" + Integer.toHexString(locctr).toUpperCase() + "\n");
            }

            found = false;

            try (Scanner optabScanner = new Scanner(optabFile)) {
                while (optabScanner.hasNextLine()) {
                    String line = optabScanner.nextLine();
                    String[] parts = line.split("-");
                    if (parts.length >= 2) {
                        code = parts[0];
                        mnemonic = parts[1];

                        if (opcode.equals(code)) {
                            locctr += 3;
                            found = true;
                            break;
                        }
                    }
                }
            }

            if (!found) {
                if (opcode.equals("WORD")) {
                    locctr += 3;
                } else if (opcode.equals("RESW")) {
                    locctr += 3 * Integer.parseInt(operand);
                } else if (opcode.equals("BYTE")) {
                    locctr++;
                } else if (opcode.equals("RESB")) {
                    locctr += Integer.parseInt(operand);
                }
            }

            if (inputScanner.hasNext()) {
                label = inputScanner.next();
                opcode = inputScanner.next();
                operand = inputScanner.next();
            }
        }

        intermediateWriter.write(Integer.toHexString(locctr).toUpperCase() + "\t" + label + "\t" + opcode + "\t" + operand + "\n");
        inputScanner.close();
        symtabWriter.close();
        intermediateWriter.close();
    }
    catch (NoSuchElementException ex) {
            JOptionPane.showMessageDialog(null, "Error processing input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    catch (NullPointerException x) {
        x.printStackTrace();
    }
    length =  locctr - start;
    return length;
}

public static void passTwo(String label, String opcode, String operand, String code, String mnemonic, int programLength) throws IOException{
    String objcode = "", sym_addr;
    String start_addr = "", addr = " ";
    String program_name = "";
    String text_record="";
    int text_len = 0, instruction_count = 0, text_record_length = 0;

    BufferedReader br1 = new BufferedReader(new FileReader(intermediateFile));
    BufferedReader br2 = new BufferedReader(new FileReader(optabFile));
    BufferedReader br3 = new BufferedReader(new FileReader(symtabFile));
    BufferedWriter objcodeWriter = new BufferedWriter(new FileWriter("objectcode.txt"));
    String[] parts;
    String line = br1.readLine();
    if (line != null && !line.trim().isEmpty()) {
        parts = line.split("\t");

        label = parts[0];
        opcode = parts[1];
        operand = parts[2];

        if (opcode.equals("START")) {
            program_name = label;
            start_addr = operand;
            String lengthInHex = String.format("%06X", programLength);
            objcodeWriter.write("\nH^" + program_name + "^00" + start_addr + "^" + lengthInHex + "\n");
            line = br1.readLine();
            parts = line.split("\t");
            start_addr = parts[0];
            label = parts[1];
            opcode = parts[2];
            operand = parts[3];
        }    
        String str1,str2;
        text_record = "T^00" + start_addr;  
        int len = 0;
        while (!opcode.equals("END")) {
            if (opcode.charAt(0) != '.') { 
                br2 = new BufferedReader(new FileReader("optab.txt"));
                boolean found = false;

                while ((line = br2.readLine()) != null) {
                    parts = line.split("-");
                    if (parts.length >= 2) {
                        code = parts[0];
                        mnemonic = parts[1];
                        if (opcode.equals(code)) {
                            found = true;
                            break;
                        }
                    }
                }
                if (found) {
                    if (!operand.equals("-")) {
                        br3 = new BufferedReader(new FileReader("symtab.txt"));
                        while ((line = br3.readLine()) != null) {
                            parts = line.split("\t");
                            if (operand.equals(parts[0])) {
                                operand = parts[1];
                                break;
                            }
                        }
                    } else {
                        operand = "00";
                    }
                    objcode = mnemonic + operand;
                } else if (opcode.equals("BYTE")) {
                    if (operand.startsWith("C'")) {
                        for (int i = 2; i < operand.length() - 1; i++) {
                            objcode += String.format("%02X", (int) operand.charAt(i));
                        }
                        len += operand.length() - 3;
                    } else if (operand.startsWith("X'")) {
                        objcode = operand.substring(2, operand.length() - 1);
                        len += objcode.length() / 2;
                    }
                } else if (opcode.equals("WORD")) {
                    objcode = String.format("%06X", Integer.parseInt(operand));
                    len += 3;
                } else {
                    objcode = "";
                }
                if (len + objcode.length() / 2 > 30 || instruction_count == 5) {
                    text_record += "^" + String.format("%02X", text_record_length / 2) ;
                    objcodeWriter.write(formatTex(text_record));
                    text_record = "\nT^00" + addr;
                    len = 0;
                    instruction_count = 0;
                    text_record_length = 0;
                }

                text_record += "^" + objcode;
                text_record_length += objcode.length(); 
                instruction_count++;
            }
            line = br1.readLine();
            if (line != null) {
                parts = line.split("\t");
                addr = parts[0];
                label = parts[1];
                opcode = parts[2];
                operand = parts[3];
            }
        }
        if (instruction_count > 0) {
            text_record += "^" + String.format("%02X", text_record_length / 2);
        }
        objcodeWriter.write(formatTex(text_record));
        objcodeWriter.write("\nE^00" + start_addr + "\n");
        br1.close();
        br2.close();
        br3.close();
        objcodeWriter.flush();
        objcodeWriter.close();
    }
}

public static String formatTex(String input) {
     String result="";
    if(input.length() >0){
        String lastTwoChars = input.substring(input.length() - 2);
        String frontPart = input.substring(0, 9);
        String restPart = input.substring(9);
        String trimmedRestPart = restPart.substring(0, restPart.lastIndexOf('^'));
        result = frontPart +" ^ "+ lastTwoChars + "^ " + trimmedRestPart;
    
        }  return result;
    }
}