//
//  NewDeclarationLineViewController.m
//  Ilmoitus
//
//  Created by Alexander Bolhuis on 15-05-14.
//  Copyright (c) 2014 42IN12EWa. All rights reserved.
//

#import "NewDeclarationLineViewController.h"
#import "Declaration.h"
#import "DeclarationLine.h"
#import "Attachment.h"
#import "NewDeclarationViewController.h"

@interface NewDeclarationLineViewController ()
@property (weak, nonatomic) IBOutlet UIButton *add;
@property (weak, nonatomic) IBOutlet UIButton *cancel;
@property (weak, nonatomic) IBOutlet UITextField *dateField;
@property (weak, nonatomic) IBOutlet UITextField *typeField;
@property (weak, nonatomic) IBOutlet UITextField *subtypeField;
@property (weak, nonatomic) IBOutlet UITextField *costField;
@property (weak, nonatomic) IBOutlet UITextField *costDecimalField;
@property (weak, nonatomic) IBOutlet UITextField *commentField;
@property (nonatomic) UIDatePicker *pktStatePicker;
@property (nonatomic) UIToolbar *mypickerToolbar;
@property (nonatomic) UIActionSheet *pickerViewPopup;
@end

@implementation NewDeclarationLineViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

-(void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex
{
    switch (buttonIndex) {
        case 0:
            [self addAttachmentFromPhotoLibrary];
            break;
        case 1:
            [self addAttachmentFromCamera];
            break;
        default:
            break;
    }
}

- (IBAction)addAttachment:(id)sender {
    [[[UIActionSheet alloc] initWithTitle:nil delegate:self cancelButtonTitle:@"Close" destructiveButtonTitle:nil otherButtonTitles:@"Choose existing", @"Create new",  nil]showInView:self.view];
}

-(void)addAttachmentFromPhotoLibrary
{
    if([UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypePhotoLibrary])
    {
        imagePicker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
        [self presentImagePicker];
    }
    else
    {
        UIAlertView *alert = [[UIAlertView alloc]initWithTitle:@"Error" message:@"Photo Library is not available" delegate:nil cancelButtonTitle:@"Close" otherButtonTitles:nil];
        [alert show];
    }
}

-(void)addAttachmentFromCamera
{
    if([UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypeCamera])
    {
        imagePicker.sourceType = UIImagePickerControllerSourceTypeCamera;
        [self presentImagePicker];
    }
    else
    {
        UIAlertView *alert = [[UIAlertView alloc]initWithTitle:@"Error" message:@"Camera is not available" delegate:nil cancelButtonTitle:@"Close" otherButtonTitles:nil];
        [alert show];
    }
}

-(void)presentImagePicker
{
    [self presentViewController:imagePicker animated:YES completion:nil];
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField {
    [textField resignFirstResponder];
    return YES;
}

-(void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary *)info
{
    NSURL *imageURL = (NSURL *)[info valueForKey:UIImagePickerControllerReferenceURL];
    
    UIImage *image = (UIImage *)[info valueForKey:UIImagePickerControllerOriginalImage];
    
    self.attachment = [[Attachment alloc]init];
    [self.attachment SetAttachmentDataFromImage:image];
    self.attachment.name = [imageURL path];
    
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (void)viewDidLoad
{
    imagePicker = [[UIImagePickerController alloc]init];
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.dateField.delegate = self;
    self.costField.delegate = self;
    self.costField.keyboardType = UIKeyboardTypeNumberPad;
    self.costDecimalField.delegate = self;
    self.costDecimalField.keyboardType = UIKeyboardTypeNumberPad;
    self.commentField.delegate = self;
    [self.commentField setReturnKeyType: UIReturnKeyDone];
    imagePicker.delegate = self;
    
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc]
                                   initWithTarget:self
                                   action:@selector(dismissKeyboard)];
    
    [self.view addGestureRecognizer:tap];
    
    // Create blank line
    
    // TODO get date from datepicker(datefField) in right format
    self.dateField.text = @"15-05-2014";
    self.declarationLine = [[DeclarationLine alloc] init];
}

-(void)dismissKeyboard {
    [self.costField resignFirstResponder];
    [self.costDecimalField resignFirstResponder];
    [self.commentField resignFirstResponder];
}


-(void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    if (sender == self.add)
    {
        self.declarationLine.cost = [self.costField.text floatValue] + ([self.costDecimalField.text floatValue] / 100);
        self.declarationLine.date = self.dateField.text;    }
    else if (sender == self.cancel)
    {
        self.declarationLine = nil;
        self.attachment = nil;
    }
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void)textFieldDidBeginEditing:(UITextField*)textField
{
    if (textField == self.dateField) {
        [self.dateField resignFirstResponder];
        
        self.pickerViewPopup = [[UIActionSheet alloc] initWithTitle:nil delegate:self cancelButtonTitle:nil destructiveButtonTitle:nil otherButtonTitles:nil];
        
        self.pktStatePicker = [[UIDatePicker alloc] initWithFrame:CGRectMake(0, 44, 0, 0)];
        self.pktStatePicker.datePickerMode = UIDatePickerModeDate;
        self.pktStatePicker.hidden = NO;
        self.pktStatePicker.date = [NSDate date];
        
        self.mypickerToolbar = [[UIToolbar alloc] initWithFrame:CGRectMake(0, 0, 320, 44)];
        self.mypickerToolbar.tintColor = [UIColor whiteColor];
        self.mypickerToolbar.barTintColor = [UIColor colorWithRed:(189/255.0) green:(26/255.0) blue:(47/255.0) alpha:1.0];
        [self.mypickerToolbar sizeToFit];
        
        NSMutableArray *barItems = [[NSMutableArray alloc] init];
        
        UIBarButtonItem *flexSpace = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFlexibleSpace target:self action:nil];
        [barItems addObject:flexSpace];
        
        UIBarButtonItem *doneBtn = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:self action:@selector(doneButtonPressed)];
        [barItems addObject:doneBtn];
        
        UIBarButtonItem *cancelBtn = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemCancel target:self action:@selector(cancelButtonPressed)];
        [barItems addObject:cancelBtn];
        
        [self.mypickerToolbar setItems:barItems animated:YES];
        
        [self.pickerViewPopup addSubview:self.mypickerToolbar];
        [self.pickerViewPopup addSubview:self.pktStatePicker];
        [self.pickerViewPopup showInView:self.view];
        [self.pickerViewPopup setBounds:CGRectMake(0,0,320, 464)];
    }
}

- (BOOL)textField:(UITextField *)textField shouldChangeCharactersInRange:(NSRange)range replacementString:(NSString *)string {
    if (textField == self.costField) {
        if (textField.text.length >= 10 && range.length == 0)
        {
            return NO;
        }
        else
        {
            return YES;
        }
    } else if (textField == self.costDecimalField) {
        if (textField.text.length >= 2 && range.length == 0)
        {
            return NO;
        }
        else
        {
            return YES;
        }
    } else {
        return YES;
    }
}

-(void)doneButtonPressed
{
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    [formatter setDateFormat:@"yyyy-MM-dd' 'HH:mm:ss.S"];
    
    NSString *stringFromDate = [formatter stringFromDate:self.pktStatePicker.date];
    self.declarationLine.date = stringFromDate;
    
    [self.dateField setText: stringFromDate];
    [self.pickerViewPopup dismissWithClickedButtonIndex:1 animated:YES];
}

-(void)cancelButtonPressed
{
    [self.pickerViewPopup dismissWithClickedButtonIndex:1 animated:YES];
}
@end