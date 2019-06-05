import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.JTextArea;

class PCB{
	public String pname;
	public List<Instruction> pInstructions;
	public int CurrentInstruction;
	PCB(){
		this.pInstructions = new ArrayList<Instruction>();
	}
}

class Instruction{
	public char IName;
	public double IRemainTime;
	public double IRuntime;
	Instruction(){
	}	
}

public class MainUI extends JFrame {

	private JPanel contentPane;
	JTextField text_timeslice = new JTextField();
	JTextField textRunningProcess = new JTextField();
	JTextPane text_ready_queue = new JTextPane();
	JTextPane text_iwait_queue = new JTextPane();
	JTextPane text_other_queue = new JTextPane();
	JTextPane text_owait_queue = new JTextPane();
	JTextField text_count = new JTextField();
	Timer time = new Timer();
	int count = 0;
	int num = 0;
	//创建各种队列，用ArrayList实现
	List<PCB> AllQueue = new ArrayList<PCB>();
	List<PCB> ReadyQueue = new ArrayList<PCB>();
	List<PCB> InputWaitingQueue = new ArrayList<PCB>();
	List<PCB> OutputWaitingQueue = new ArrayList<PCB>();
	List<PCB> PureWaitingQueue = new ArrayList<PCB>();
	
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainUI frame = new MainUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 956, 621);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel label = new JLabel("\u65F6\u95F4\u7247:");
		label.setBounds(650, 53, 60, 18);
		contentPane.add(label);
		
		JButton btnOpenFile = new JButton("\u6253\u5F00\u6587\u4EF6");
		btnOpenFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
                if (chooser.showOpenDialog(btnOpenFile)==JFileChooser.APPROVE_OPTION) {//
                    File file = chooser.getSelectedFile();
                    textRunningProcess.setText(file.getName());
                    readFile(file);
                };
			}
		});
		btnOpenFile.setBounds(76, 49, 113, 27);
		contentPane.add(btnOpenFile);
		
		//开始调度按钮事件
		JButton btnStartSchedule = new JButton("\u5F00\u59CB\u8C03\u5EA6");
		btnStartSchedule.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(text_timeslice.getText().equals("")) {
					JOptionPane.showMessageDialog(null,"请输入时间片大小!");
				}
				else {
					TimerTask task = new TimerTask() {
						public void run() {
							RunOneTime();
							
							text_count.setText(String.valueOf(count));
						}
					};
					btnStartSchedule.setEnabled(false);
					time.schedule(task,new Date(),Long.parseLong(text_timeslice.getText()) );
				}
			}
		});
		btnStartSchedule.setBounds(253, 49, 113, 27);
		contentPane.add(btnStartSchedule);
		
		//暂停调度按钮事件
		JButton btnPauseSchedule = new JButton("\u6682\u505C\u8C03\u5EA6");
		btnPauseSchedule.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				time.cancel();
			}
		});
		btnPauseSchedule.setBounds(438, 49, 113, 27);
		contentPane.add(btnPauseSchedule);
		
		
		text_timeslice.setBounds(714, 50, 113, 24);
		contentPane.add(text_timeslice);
		text_timeslice.setColumns(10);
		
		JLabel label_1 = new JLabel("\u5F53\u524D\u8FD0\u884C\u8FDB\u7A0B:");
		label_1.setBounds(76, 108, 103, 18);
		contentPane.add(label_1);
		
		
		textRunningProcess.setBounds(76, 139, 103, 24);
		contentPane.add(textRunningProcess);
		textRunningProcess.setColumns(10);
		
		JLabel label_2 = new JLabel("\u5C31\u7EEA\u961F\u5217");
		label_2.setBounds(57, 205, 72, 18);
		contentPane.add(label_2);
		
		JLabel label_3 = new JLabel("\u8F93\u5165\u7B49\u5F85\u961F\u5217");
		label_3.setBounds(291, 205, 96, 18);
		contentPane.add(label_3);
		
		JLabel label_4 = new JLabel("\u8F93\u51FA\u7B49\u5F85\u961F\u5217");
		label_4.setBounds(497, 205, 96, 18);
		contentPane.add(label_4);
		
		JLabel label_5 = new JLabel("\u5176\u5B83\u7B49\u5F85\u961F\u5217");
		label_5.setBounds(721, 205, 96, 18);
		contentPane.add(label_5);
		
		text_ready_queue.setBounds(43, 236, 146, 205);
		contentPane.add(text_ready_queue);
		
		
		text_iwait_queue.setBounds(273, 236, 146, 205);
		contentPane.add(text_iwait_queue);
		
		text_owait_queue.setBounds(490, 236, 146, 205);
		contentPane.add(text_owait_queue);
		
		
		text_other_queue.setBounds(704, 236, 146, 205);
		contentPane.add(text_other_queue);
		
		
		text_count.setBounds(257, 139, 109, 24);
		contentPane.add(text_count);
		text_count.setColumns(10);
		
		JLabel label_6 = new JLabel("\u5DF2\u8C03\u5EA6\u6B21\u6570");
		label_6.setBounds(253, 108, 90, 18);
		contentPane.add(label_6);
		
		
		
	}
	public void readFile(File file){//读文件
        BufferedReader bReader;
        try {
            bReader=new BufferedReader(new FileReader(file));
            StringBuffer sBuffer=new StringBuffer();
            String str;
            while((str=bReader.readLine())!=null){
                sBuffer.append(str+'\n');
                switch(str.charAt(0)) {
                case 'P':
                	PCB p = new PCB();
                	p.pname = str;
                	AllQueue.add(p);
                	break;
                case 'C':
                	Instruction ins1 = new Instruction();
                	ins1.IRemainTime = Integer.parseInt(str.substring(1,3));
                	ins1.IName = 'C';
                	AllQueue.get(AllQueue.size()-1).pInstructions.add(ins1);
                	break;
                case 'I':
                	Instruction ins2 = new Instruction();
                	ins2.IRemainTime = Integer.parseInt(str.substring(1,3));
                	ins2.IName = 'I';
                	AllQueue.get(AllQueue.size()-1).pInstructions.add(ins2);
                	break;
                case 'O':
                	Instruction ins3 = new Instruction();
                	ins3.IRemainTime = Integer.parseInt(str.substring(1,3));
                	ins3.IName = 'O';
                	AllQueue.get(AllQueue.size()-1).pInstructions.add(ins3);
                	break;
                case 'W':
                	Instruction ins4 = new Instruction();
                	ins4.IRemainTime = Integer.parseInt(str.substring(1,3));
                	ins4.IName = 'W';
                	AllQueue.get(AllQueue.size()-1).pInstructions.add(ins4);
                	break;
                case 'H':
                	Instruction ins5 = new Instruction();
                	ins5.IRemainTime = Integer.parseInt(str.substring(1,3));
                	ins5.IName = 'H';
                	AllQueue.get(AllQueue.size()-1).pInstructions.add(ins5);
                	break;
                }//switch                     
            }//while
            
            
//            String s1 = "";
//            for(PCB p:ReadyQueue) {
//            	s1 = s1+p.pname+"\r\n";
//            }
//            text_ready_queue.setText(s1);
            
            
            //根据每个进程的首指令将进程分配到各个队列中
           
//            for(PCB p:AllQueue) {
//             	switch(p.pInstructions.get(0).IName) {
//             	case 'C':
//             		ReadyQueue.add(p);
//             		break;
//             	case 'I':
//             		InputWaitingQueue.add(p);
//             		break;
//             	case 'O':
//             		OutputWaitingQueue.add(p);
//             		break;
//             	case 'W':
//             		PureWaitingQueue.add(p);
//             		break;
//             	case 'H':
//             		break;
//             	}
//             }
//            
            String s1 = "";
          for(PCB p:AllQueue) {
          	s1 = s1+p.pname+"\r\n";
          }
          text_ready_queue.setText(s1);
            //将界面正确显示
//            String s1 = "";
//            for(PCB p:ReadyQueue) {
//            	s1 = s1+p.pname+"\r\n";
//            }
//            text_ready_queue.setText(s1);
//            
//            String s2 = "";
//            for(PCB p:InputWaitingQueue) {
//            	s2 = s2+p.pname+"\r\n";
//            }
//            text_iwait_queue.setText(s2);
//            
//            String s3 = "";
//            for(PCB p:OutputWaitingQueue) {
//            	s3 = s3+p.pname+"\r\n";
//            }
//            text_owait_queue.setText(s3);
//            
//            String s4 = "";
//            for(PCB p:PureWaitingQueue) {
//            	s4 = s4+p.pname+"\r\n";
//            }
//            text_other_queue.setText(s4);
                     
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
	
	
	//每隔一个时间间隔运行一次
	public void RunOneTime() {
		int size = AllQueue.size();
		if(num<size) {
			PCB p = AllQueue.get(num);
			switch(p.pInstructions.get(0).IName) {
	     	case 'C':
	     		ReadyQueue.add(p);
	     		break;
	     	case 'I':
	     		InputWaitingQueue.add(p);
	     		break;
	     	case 'O':
	     		OutputWaitingQueue.add(p);
	     		break;
	     	case 'W':
	     		PureWaitingQueue.add(p);
	     		break;
	     	case 'H':
	     		break;
	     	}
			num++;
		}
		
		//只要有队列不为空就一直调度
		if(ReadyQueue.size()!=0||InputWaitingQueue.size()!=0||OutputWaitingQueue.size()!=0||PureWaitingQueue.size()!=0) {
			
			//只有一个CPU，所以只能对就绪队列的第一个PCB的运行指令时间进行减一操作
			if(ReadyQueue.size()!=0) {
				if(ReadyQueue.get(0).pInstructions.get(ReadyQueue.get(0).CurrentInstruction).IRemainTime==0) {
					ReadyQueue.get(0).CurrentInstruction++;
					switch(ReadyQueue.get(0).pInstructions.get(ReadyQueue.get(0).CurrentInstruction).IName) {
					case 'C':
						ReadyQueue.add(ReadyQueue.get(0));//先加再删，ArrayList的好处，动态添加和删除，[1,2,3]把1删了再加，就是[2,,3,1]了
						ReadyQueue.remove(ReadyQueue.get(0));
						break;
					case 'I':
						InputWaitingQueue.add(ReadyQueue.get(0));
						ReadyQueue.remove(ReadyQueue.get(0));
						break;
					case 'O':
						OutputWaitingQueue.add(ReadyQueue.get(0));
						ReadyQueue.remove(ReadyQueue.get(0));
						break;
					case 'W':
						PureWaitingQueue.add(ReadyQueue.get(0));
						ReadyQueue.remove(ReadyQueue.get(0));
						break;
					case 'H':
						ReadyQueue.remove(ReadyQueue.get(0));
						break;
					}
				}
				else {
					ReadyQueue.get(0).pInstructions.get(ReadyQueue.get(0).CurrentInstruction).IRemainTime--;
					textRunningProcess.setText(ReadyQueue.get(0).pname);
					ReadyQueue.add(ReadyQueue.get(0));
					ReadyQueue.remove(ReadyQueue.get(0));
				}
			}
			
			//输入等待队列和输出等待队列以及其它等待队列之所以采用for循环是因为任务书上说I/O设备不限，所以可以对这几个队列的每个PCB进行减一
			if(InputWaitingQueue.size()!=0) {
				for(int i=0;i<=InputWaitingQueue.size()-1;i++) {
					if(InputWaitingQueue.get(i).pInstructions.get(InputWaitingQueue.get(i).CurrentInstruction).IRemainTime==0) {
						InputWaitingQueue.get(i).CurrentInstruction++;
						switch(InputWaitingQueue.get(i).pInstructions.get(InputWaitingQueue.get(i).CurrentInstruction).IName) {
						case 'C':
							ReadyQueue.add(InputWaitingQueue.get(i));
							InputWaitingQueue.remove(InputWaitingQueue.get(i));
							break;
						case 'I':
							InputWaitingQueue.add(InputWaitingQueue.get(i));
							InputWaitingQueue.remove(InputWaitingQueue.get(i));
							break;
						case 'O':
							OutputWaitingQueue.add(InputWaitingQueue.get(i));
							InputWaitingQueue.remove(InputWaitingQueue.get(i));
							break;
						case 'W':
							PureWaitingQueue.add(InputWaitingQueue.get(i));
							InputWaitingQueue.remove(InputWaitingQueue.get(i));
							break;
						case 'H':
							InputWaitingQueue.remove(InputWaitingQueue.get(i));
							break;
						}
					}
					else {
						InputWaitingQueue.get(i).pInstructions.get(InputWaitingQueue.get(i).CurrentInstruction).IRemainTime--;
					}
				}
			}
			
			if(OutputWaitingQueue.size()!=0) {
				for(int i=0;i<=OutputWaitingQueue.size()-1;i++) {
					if(OutputWaitingQueue.get(i).pInstructions.get(OutputWaitingQueue.get(i).CurrentInstruction).IRemainTime==0) {
						OutputWaitingQueue.get(i).CurrentInstruction++;
						switch(OutputWaitingQueue.get(i).pInstructions.get(OutputWaitingQueue.get(i).CurrentInstruction).IName) {
						case 'C':
							ReadyQueue.add(OutputWaitingQueue.get(i));
							OutputWaitingQueue.remove(OutputWaitingQueue.get(i));
							break;
						case 'I':
							InputWaitingQueue.add(OutputWaitingQueue.get(i));
							OutputWaitingQueue.remove(OutputWaitingQueue.get(i));
							break;
						case 'O':
							OutputWaitingQueue.add(OutputWaitingQueue.get(i));
							OutputWaitingQueue.remove(OutputWaitingQueue.get(i));
							break;
						case 'W':
							PureWaitingQueue.add(OutputWaitingQueue.get(i));
							OutputWaitingQueue.remove(OutputWaitingQueue.get(i));
							break;
						case 'H':
							OutputWaitingQueue.remove(OutputWaitingQueue.get(i));
							break;
						}
					}
					else {
						OutputWaitingQueue.get(i).pInstructions.get(OutputWaitingQueue.get(i).CurrentInstruction).IRemainTime--;
					}
				}
			}
			
			if(PureWaitingQueue.size()!=0) {
				for(int i=0;i<=PureWaitingQueue.size()-1;i++) {
					if(PureWaitingQueue.get(i).pInstructions.get(PureWaitingQueue.get(i).CurrentInstruction).IRemainTime==0) {
						PureWaitingQueue.get(i).CurrentInstruction++;
						switch(PureWaitingQueue.get(i).pInstructions.get(PureWaitingQueue.get(i).CurrentInstruction).IName) {
						case 'C':
							ReadyQueue.add(PureWaitingQueue.get(i));
							PureWaitingQueue.remove(PureWaitingQueue.get(i));
							break;
						case 'I':
							InputWaitingQueue.add(PureWaitingQueue.get(i));
							PureWaitingQueue.remove(PureWaitingQueue.get(i));
							break;
						case 'O':
							OutputWaitingQueue.add(PureWaitingQueue.get(i));
							PureWaitingQueue.remove(PureWaitingQueue.get(i));
							break;
						case 'W':
							PureWaitingQueue.add(PureWaitingQueue.get(i));
							PureWaitingQueue.remove(PureWaitingQueue.get(i));
							break;
						case 'H':
							PureWaitingQueue.remove(PureWaitingQueue.get(i));
							break;
						}
					}
					else {
						PureWaitingQueue.get(i).pInstructions.get(PureWaitingQueue.get(i).CurrentInstruction).IRemainTime--;
					}
				}
			}
			
			//所有用到CPU资源的PCB都没了就清空这个控件
			if(ReadyQueue.size()==0) {
				textRunningProcess.setText("");
			}
			
			//每进行一次调度就更新一次控件的信息
			String str1 = "";
			for(int i=0;i<ReadyQueue.size();i++) {
				str1 = str1+ReadyQueue.get(i).pname+"\r\n";
			}
			text_ready_queue.setText(str1);
			
			String str2 = "";
			for(int i=0;i<InputWaitingQueue.size();i++) {
				str2 = str2+InputWaitingQueue.get(i).pname+"\r\n";
			}
			text_iwait_queue.setText(str2);
			
			String str3 = "";
			for(int i=0;i<OutputWaitingQueue.size();i++) {
				str3 = str3+OutputWaitingQueue.get(i).pname+"\r\n";
			}
			text_owait_queue.setText(str3);
			
			String str4 = "";
			for(int i=0;i<PureWaitingQueue.size();i++) {
				str4 = str4+PureWaitingQueue.get(i).pname+"\r\n";
			}
			text_other_queue.setText(str4);
			
			count++;
			
			String schedule = "第"+String.valueOf(count)+"次调度情况:\r\n"+"就绪队列:\r\n"+str1+"\r\n"+"输入等待队列:\r\n"+str2+"\r\n"
										+"输出等待队列:\r\n"+str3+"\r\n"+"其它等待队列:\r\n"+str4+"\r\n";
			writeFile(schedule);
		}
		else {
			JOptionPane.showMessageDialog(null, "调度结束，已将调度过程输出到日志文件中！");
			time.cancel();
		}
	}
	public static void writeFile(String text) {
        try {
            File writeName = new File("D:/123课设Excel/output.txt"); // 相对路径，如果没有则要建立一个新的output.txt文件
//            writeName.createNewFile(); // 创建新文件,有同名的文件的话直接覆盖
            try (FileWriter writer = new FileWriter(writeName,true);//追加
                 BufferedWriter out = new BufferedWriter(writer)
            ) {
                out.write(text); 
                out.flush(); // 把缓存区内容压入文件
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
