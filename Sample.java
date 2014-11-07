import java.awt.*;
import java.io.IOException;


import com.leapmotion.leap.*;
import com.leapmotion.leap.Frame;

import java.lang.Math;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

class DialGUI extends JFrame {
	private JTextArea textArea;
	private JScrollPane phoneDirscrollPane;
	private JScrollPane callHisscrollPane;
	private JList phoneDirectorylist;
	private JList callHistorylist;
	private JTabbedPane switchTabPane;
	private int listSize1;
	private int listSize2;
	private static final long serialVersionUID = -8534844170998963067L;

	public DialGUI() {
		SampleListener listener = new SampleListener();
		Controller controller = new Controller();
		controller.addListener(listener);
		AddphonedirectoryListmodel();
		AddcallhistoryListmodel();
		JPanel pane1 = new JPanel();
		JPanel pane2 = new JPanel();
		pane1.setLayout(new GridLayout(1, 1));
		pane2.setLayout(new GridLayout(1, 1));
		phoneDirscrollPane = new JScrollPane();
		phoneDirscrollPane.setViewportView(phoneDirectorylist);
		callHisscrollPane= new JScrollPane();
		callHisscrollPane.setViewportView(callHistorylist);
		pane1.add(phoneDirscrollPane);
		pane2.add(callHisscrollPane);
		
		switchTabPane=new JTabbedPane();
		switchTabPane.addTab("phonedirectory", pane1);
		switchTabPane.add("callhistory", pane2);
		switchTabPane.add("dial",null);
		
		phoneDirectorylist.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e){
			int curIndex = phoneDirectorylist.getSelectedIndex();
		    int firstVisibleIndex=phoneDirectorylist.getFirstVisibleIndex();
		    int lastVisibleIndex=phoneDirectorylist.getLastVisibleIndex();
		    if (curIndex<firstVisibleIndex) {
		    	JScrollBar scrollbar=phoneDirscrollPane.getVerticalScrollBar();
		    	scrollbar.setValue(scrollbar.getValue()-50);
		    }
		    else if (curIndex>lastVisibleIndex)
		    {   JScrollBar scrollbar=phoneDirscrollPane.getVerticalScrollBar();
	    	    scrollbar.setValue(scrollbar.getValue()+50);
			}
		}
		});
		callHistorylist.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e){
			int curIndex = callHistorylist.getSelectedIndex();
		    int firstVisibleIndex=callHistorylist.getFirstVisibleIndex();
		    int lastVisibleIndex=callHistorylist.getLastVisibleIndex();
		    if (curIndex<firstVisibleIndex) {
		    	JScrollBar scrollbar=callHisscrollPane.getVerticalScrollBar();
		    	scrollbar.setValue(scrollbar.getValue()-50);
		    }
		    else if (curIndex>lastVisibleIndex)
		    {   JScrollBar scrollbar=callHisscrollPane.getVerticalScrollBar();
	    	    scrollbar.setValue(scrollbar.getValue()+50);
			}
		}
		});
		add(switchTabPane);
//		add(pane);
		setTitle("Gestrue Controll Dial System");
		setSize(400, 400);
		setVisible(true);
		

	}

	private class SampleListener extends Listener {
		private float biasPosition;

		public void onConnect(Controller controller) {
			// Note: not dispatched when running in a debugger.
		}

		public void onFrame(Controller controller) {
			Frame frame = controller.frame();
			listviewControll(frame);
			detectSwitchDeleteGesture(controller);

		}

		public void onExit(Controller controller) {
			System.out.println("Exited");
		}
		public void listviewControll(Frame frame)
		{	
			Hand hand = frame.hands().get(0);
			int extendedFingers = 0;

			for (Finger finger : hand.fingers()) {
				if (finger.isExtended())
					extendedFingers++;
			}
//			if (extendedFingers == 5)
//				System.out.println("fingers extented");
			Finger indexFinger = hand.fingers()
					.fingerType(Finger.Type.TYPE_INDEX).get(0);
			Vector indexFingerSpeed = indexFinger.tipVelocity();
			Vector normal = hand.palmNormal();
			Vector direction = hand.direction();
			double pitchDegree = Math.toDegrees(direction.pitch());
			double strollSpeed = 0;
			if (extendedFingers == 5) {
				if (pitchDegree > 10 || pitchDegree < -10)
					strollSpeed = pitchDegree * 0.007;
			}
			biasPosition -= strollSpeed;
			if (biasPosition >= 1) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						ListModeladjust(true);
					}
				});
				biasPosition -= 1;
			}
			if (biasPosition <= -1) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						ListModeladjust(false);
					}
				});
				biasPosition += 1;
			}
			
		}
		public void detectSwitchDeleteGesture(Controller controller) {
			Frame frame = controller.frame();
			Frame lastframe = controller.frame(1);
			if (detectPalmExtended(frame) && detectPalmExtended(lastframe)) {
//				System.out.println("Palm extented");
				if (detectMoveleft(controller)) {
					int switchindex=switchTabPane.getSelectedIndex()-1;
					if (switchindex<0) switchindex+=3;
					switchTabPane.setSelectedIndex(switchindex);
					
				}
				if (detectMoveright(controller)) {
					int switchindex=switchTabPane.getSelectedIndex()+1;
					if (switchindex>2) switchindex-=3;
					switchTabPane.setSelectedIndex(switchindex);
					}
				}
			if (detectGestureTwo(frame)&&detectGestureTwo(lastframe)){
				if (detectMoveleft(controller)) System.out.println("detect deleted gesture");
				if (detectMoveright(controller)) System.out.println("detect recover gestrue");
			}
		}
	    public boolean detectMoveright(Controller controller){
			Frame frame = controller.frame();
			Frame lastframe = controller.frame(1);
	    	Hand hand = frame.hands().get(0);
			float palmVelocity = hand.palmVelocity().getX();
			Hand lasthand = lastframe.hands().get(0);
			float lastpalmVelocity = lasthand.palmVelocity().getX();
			if (palmVelocity < 0 && lastpalmVelocity > 0) {
				float avergVelocity = 0;
				int i;
				for (i = 1; i <= 21; i++) {
					lastframe = controller.frame(i);
					hand = lastframe.hands().get(0);
					avergVelocity += hand.palmVelocity().getX();
				}
				avergVelocity = avergVelocity / 20;
				if (avergVelocity > 150){
					if (frame.hands().get(0).palmPosition().getX()>60) return true;
					else return false;}
				else 
					return false;
					
	    	
	    }
			else return false;
	    }
	  public boolean detectMoveleft(Controller controller){
			Frame frame = controller.frame();
			Frame lastframe = controller.frame(1);
	    	Hand hand = frame.hands().get(0);
			float palmVelocity = hand.palmVelocity().getX();
			Hand lasthand = lastframe.hands().get(0);
			float lastpalmVelocity = lasthand.palmVelocity().getX();
			if (palmVelocity > 0 && lastpalmVelocity < 0) {
				float avergVelocity = 0;
				int i;
				for (i = 1; i <= 21; i++) {
					lastframe = controller.frame(i);
					hand = lastframe.hands().get(0);
					avergVelocity += hand.palmVelocity().getX();
				}
				avergVelocity = avergVelocity / 20;
				if (avergVelocity < -150){
					if (frame.hands().get(0).palmPosition().getX()<-60) return true;
					else return false;}
				else 
					return false;
					
	    	
	    }
			else return false;
	    } 
	   public boolean detectPalmExtended(Frame frame){
	    	Hand hand =frame.hands().get(0);
	    	int extendedFingers=0;
	        for (Finger finger : hand.fingers()){
	        	if(finger.isExtended()) extendedFingers++;
	        	
	        }
	         if(extendedFingers==5){ 
//	        	 System.out.println("finger extended");
	        	 return true;
	         }
	         else
	        	 return false;
	         
	    }
		public boolean detectGestureTwo(Frame frame) {
			Hand hand = frame.hands().get(0);
			boolean[] extendedFinger;
			extendedFinger = new boolean[5];
			for (Finger finger : hand.fingers()) {
				switch (finger.type()) {
				case TYPE_THUMB:
					extendedFinger[0] = finger.isExtended();
				case TYPE_INDEX:
					extendedFinger[1] = finger.isExtended();
				case TYPE_MIDDLE:
					extendedFinger[2] = finger.isExtended();
				case TYPE_PINKY:
					extendedFinger[3] = finger.isExtended();
				case TYPE_RING:
					extendedFinger[4] = finger.isExtended();
				}

			}

			if (!extendedFinger[0] && extendedFinger[1] && extendedFinger[2]
					&& !extendedFinger[3] && !extendedFinger[4])
				return true;
			else
				return false;

		}

	}

	public void ListModeladjust(boolean adjPara) {
		if (switchTabPane.getSelectedIndex()==0){
		int curIndex = phoneDirectorylist.getSelectedIndex();
		if (adjPara == true && curIndex < listSize1 - 1) {
			phoneDirectorylist.setSelectedIndex(curIndex + 1);

		}
		if (adjPara == false && curIndex > 0) {
			phoneDirectorylist.setSelectedIndex(curIndex - 1);
		}
		}
		if (switchTabPane.getSelectedIndex()==1){
			int curIndex = callHistorylist.getSelectedIndex();
			if (adjPara == true && curIndex < listSize2 - 1) {
				callHistorylist.setSelectedIndex(curIndex + 1);

			}
			if (adjPara == false && curIndex > 0) {
				callHistorylist.setSelectedIndex(curIndex - 1);
			}
			}
	}

	public void AddphonedirectoryListmodel() {
		DefaultListModel listModel = new DefaultListModel();
		listModel.addElement("Xue Liu");
		listModel.addElement("Yan Shen");
		listModel.addElement("Jian Li");
		listModel.addElement("Landu Jiang");
		listModel.addElement("MingYuan Xia");
		int i;
		for (i=1;i<100;i++){
			listModel.addElement("Friend"+i);
		}

		listSize1 = listModel.getSize();
		phoneDirectorylist = new JList(listModel);
		phoneDirectorylist.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		phoneDirectorylist.setLayoutOrientation(JList.VERTICAL);
		phoneDirectorylist.setVisibleRowCount(-1);
		phoneDirectorylist.setSelectedIndex(0);

	}
	public void AddcallhistoryListmodel() {
		DefaultListModel listModel = new DefaultListModel();
		listModel.addElement("911");
		listModel.addElement("123");
		listModel.addElement("234");
		listModel.addElement("Landu Jiang");
		listModel.addElement("MingYuan Xia");
		int i;
		for (i=1;i<100;i++){
			listModel.addElement("Friend"+i);
		}

		listSize2 = listModel.getSize();
		callHistorylist = new JList(listModel);
		callHistorylist.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		callHistorylist.setLayoutOrientation(JList.VERTICAL);
		callHistorylist.setVisibleRowCount(-1);
		callHistorylist.setSelectedIndex(0);

	}

}

class Sample {
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				DialGUI dialgui = new DialGUI();
				dialgui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				dialgui.setVisible(true);

			}
		});

	}

}
